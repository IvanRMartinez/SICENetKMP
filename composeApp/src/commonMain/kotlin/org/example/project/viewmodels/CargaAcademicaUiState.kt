package org.example.project.viewmodels

import org.example.project.database.CargaEntity

data class CargaAcademicaUiState(
    val materias: List<CargaEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)