package org.example.project.repository

import org.example.project.database.CargaEntity
import org.example.project.database.FinalesEntity
import org.example.project.database.KardexEntity
import org.example.project.database.PerfilEntity
import org.example.project.database.UnidadesEntity

interface SiceRepository {

    // ==========================================
    // Funciones para el Perfil
    // ==========================================
    suspend fun getPerfil(): PerfilEntity?
    suspend fun savePerfil(matricula: String, nombre: String, carrera: String, ultimoAcceso: String?)

    // ==========================================
    // Funciones para la Carga Académica
    // ==========================================
    suspend fun getCargaAcademica(): List<CargaEntity>
    suspend fun saveCargaMateria(materia: String, docente: String, horario: String)

    // ==========================================
    // Funciones para el Kardex
    // ==========================================
    suspend fun getKardex(): List<KardexEntity>
    suspend fun saveKardexMateria(materia: String, calificacion: Int?, periodo: String)

    // ==========================================
    // Funciones para Unidades
    // ==========================================
    suspend fun getUnidades(): List<UnidadesEntity>
    suspend fun saveUnidades(materia: String, unidades: List<Int?>, promedio: String)

    // ==========================================
    // Funciones para Calificaciones Finales
    // ==========================================
    suspend fun getFinales(): List<FinalesEntity>
    suspend fun saveFinal(materia: String, acreditacion: String, calificacion: Int?, observaciones: String)

    // ==========================================
    // Utilidades
    // ==========================================
    // Nota: Esta también la hacemos suspend porque limpiar las tablas de la BD es una operación de escritura.
    suspend fun clearAll()
}