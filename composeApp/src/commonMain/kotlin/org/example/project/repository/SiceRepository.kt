package org.example.project.repository

import org.example.project.database.CargaEntity
import org.example.project.database.FinalesEntity
import org.example.project.database.KardexEntity
import org.example.project.database.PerfilEntity
import org.example.project.database.UnidadesEntity

interface SiceRepository {
    // Funciones para el Perfil
    fun getPerfil(): PerfilEntity?
    fun savePerfil(matricula: String, nombre: String, carrera: String, ultimoAcceso: String?)

    // Funciones para la Carga Académica
    fun getCargaAcademica(): List<CargaEntity>
    fun saveCargaMateria(materia: String, docente: String, horario: String)

    // Funciones para el Kardex
    fun getKardex(): List<KardexEntity>
    fun saveKardexMateria(materia: String, calificacion: Int?, periodo: String)

    // Unidades
    fun getUnidades(): List<UnidadesEntity>
    fun saveUnidades(materia: String, unidades: List<Int?>, promedio: String)

    // Finales
    fun getFinales(): List<FinalesEntity>
    fun saveFinal(materia: String, acreditacion: String, calificacion: Int?, observaciones: String)

    // Utilidad para cerrar sesión
    fun clearAll()
}