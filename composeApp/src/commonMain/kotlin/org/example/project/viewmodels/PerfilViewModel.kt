package org.example.project.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.repository.SiceRepository

class PerfilViewModel(
    private val repository: SiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        // En cuanto el ViewModel nace (cuando se abre la pantalla),
        // disparamos la carga de datos automáticamente.
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            // 1. Encendemos la ruedita de carga por si tarda
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 2. ESTRATEGIA OFFLINE-FIRST: Leemos la base de datos local
            val perfilLocal = repository.getPerfil()

            // Si ya teníamos datos guardados de una sesión anterior, los mostramos inmediatamente
            if (perfilLocal != null) {
                _uiState.update {
                    it.copy(
                        matricula = perfilLocal.matricula,
                        nombre = perfilLocal.nombre,
                        carrera = perfilLocal.carrera,
                        ultimoAcceso = perfilLocal.ultimoAcceso ?: ""
                    )
                }
            }

            // 3. Vamos a internet a buscar datos frescos (Ktor)
            val resultadoRemoto = repository.fetchRemotePerfil()

            // 4. Evaluamos qué nos respondió la escuela
            resultadoRemoto.fold(
                onSuccess = { perfilFresco ->
                    // Si hubo éxito, actualizamos la pantalla con lo más nuevo
                    // (el repositorio ya se encargó de guardarlo en la BD local)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            matricula = perfilFresco.matricula,
                            nombre = perfilFresco.nombre ,
                            carrera = perfilFresco.carrera ,
                            ultimoAcceso = perfilFresco.ultimoAcceso ?: ""
                        )
                    }
                },
                onFailure = { excepcion ->
                    // Si falla (ej. el servidor de la escuela se cayó),
                    // quitamos el loading y mostramos el error.
                    // ¡OJO! No borramos los datos de la pantalla, el alumno aún podrá ver su perfil local.
                    _uiState.update { it.copy(isLoading = false, error = excepcion.message) }
                }
            )
        }
    }

    // Un extra útil: Función para cerrar sesión desde el menú de Perfil
    fun cerrarSesion(onNavigateToLogin: () -> Unit) {
        viewModelScope.launch {
            // Limpia la base de datos y borra las cookies (lo que armamos antes)
            repository.clearAll()
            onNavigateToLogin() // Le avisamos a la vista que debe regresar al inicio
        }
    }
}