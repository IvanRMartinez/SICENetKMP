package org.example.project.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.repository.SiceRepository

class CargaAcademicaViewModel(
    private val repository: SiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CargaAcademicaUiState())
    val uiState: StateFlow<CargaAcademicaUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la pantalla de Carga Académica, disparamos la petición
        cargarMaterias()
    }

    fun cargarMaterias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 1. ESTRATEGIA OFFLINE-FIRST: Leemos la BD local
            val cargaLocal = repository.getCargaAcademica()

            // Verificamos si la lista local tiene elementos
            if (cargaLocal.isNotEmpty()) {
                _uiState.update {
                    it.copy(materias = cargaLocal)
                }
            }

            // 2. Vamos a Ktor a buscar si hubo cambios (ej. el alumno dio de alta otra materia)
            val resultadoRemoto = repository.fetchRemoteCarga()

            // 3. Evaluamos la respuesta de la escuela
            resultadoRemoto.fold(
                onSuccess = { cargaFresca ->
                    // Si todo salió bien, actualizamos la pantalla con la lista nueva.
                    // Recuerda que el repositorio ya hizo el trabajo sucio de borrar y guardar en SQLDelight.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            materias = cargaFresca
                        )
                    }
                },
                onFailure = { excepcion ->
                    // Si falla el internet, apagamos la carga y mostramos el error,
                    // pero el alumno seguirá viendo sus materias guardadas localmente.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = excepcion.message
                        )
                    }
                }
            )
        }
    }
}