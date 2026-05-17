package org.example.project.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.repository.SiceRepository

class LoginViewModel(
    private val repository: SiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onMatriculaChange(nuevaMatricula: String) {
        _uiState.update { it.copy(matricula = nuevaMatricula, error = null) }
    }

    fun onContraseniaChange(nuevaContra: String) {
        _uiState.update { it.copy(contrasenia = nuevaContra, error = null) }
    }

    fun resetNavigation() {
        _uiState.update { it.copy(navigateToHome = false) }
    }

    fun login() {
        val current = _uiState.value

        if (current.matricula.isBlank() || current.contrasenia.isBlank()) {
            _uiState.update { it.copy(error = "Llena todos los campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.sincronizarLogin(
                matricula = current.matricula,
                contrasenia = current.contrasenia,
                tipoUsuario = current.tipoUsuario
            )

            result.fold(
                onSuccess = {
                    // Encendemos la bandera de navegación
                    _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
                },
                onFailure = { excepcion ->
                    _uiState.update { it.copy(isLoading = false, error = excepcion.message) }
                }
            )
        }
    }
}