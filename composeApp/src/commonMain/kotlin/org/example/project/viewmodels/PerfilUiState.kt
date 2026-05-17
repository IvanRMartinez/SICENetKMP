package org.example.project.viewmodels

data class PerfilUiState(
    val matricula: String = "",
    val nombre: String = "",
    val carrera: String = "",
    val ultimoAcceso: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)