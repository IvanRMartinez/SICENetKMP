package org.example.project.viewmodels

// Esta data class representa todo lo que la pantalla de Login puede mostrar
data class LoginUiState(
    val matricula: String = "",
    val contrasenia: String = "",
    val tipoUsuario: String = "ALUMNO", // Por defecto
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val navigateToHome: Boolean = false
)