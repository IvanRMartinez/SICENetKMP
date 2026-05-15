package org.example.project.repository

import org.example.project.database.CargaEntity
import org.example.project.database.FinalesEntity
import org.example.project.database.KardexEntity
import org.example.project.database.PerfilEntity
import org.example.project.database.SiceDatabase
import org.example.project.database.UnidadesEntity

class SiceRepositoryImpl(private val database: SiceDatabase) : SiceRepository {

    override fun getUnidades(): List<UnidadesEntity> {
        return queries.getUnidades().executeAsList()
    }

    override fun saveUnidades(materia: String, unidades: List<Int?>, promedio: String) {
        queries.insertUnidades(
            materia = materia,
            u1 = unidades.getOrNull(0)?.toLong(),
            u2 = unidades.getOrNull(1)?.toLong(),
            u3 = unidades.getOrNull(2)?.toLong(),
            u4 = unidades.getOrNull(3)?.toLong(),
            u5 = unidades.getOrNull(4)?.toLong(),
            u6 = unidades.getOrNull(5)?.toLong(),
            u7 = unidades.getOrNull(6)?.toLong(),
            u8 = unidades.getOrNull(7)?.toLong(),
            u9 = unidades.getOrNull(8)?.toLong(),
            u10 = unidades.getOrNull(9)?.toLong(),
            promedio = promedio
        )
    }

    override fun getFinales(): List<FinalesEntity> {
        return queries.getFinales().executeAsList()
    }

    override fun saveFinal(materia: String, acreditacion: String, calificacion: Int?, observaciones: String) {
        queries.insertFinales(materia, acreditacion, calificacion?.toLong(), observaciones)
    }

    // Aquí mandamos llamar a las consultas generadas por tu archivo .sq
    private val queries = database.appDatabaseQueries

    override fun getPerfil(): PerfilEntity? {
        // executeAsOneOrNull() devuelve el dato si existe, o 'null' si la tabla está vacía
        return queries.getPerfil().executeAsOneOrNull()
    }

    override fun savePerfil(matricula: String, nombre: String, carrera: String, ultimoAcceso: String?) {
        queries.insertPerfil(matricula, nombre, carrera, ultimoAcceso)
    }

    override fun getCargaAcademica(): List<CargaEntity> {
        // executeAsList() convierte el resultado de SQL a una lista de Kotlin
        return queries.getCarga().executeAsList()
    }

    override fun saveCargaMateria(materia: String, docente: String, horario: String) {
        queries.insertCarga(materia, docente, horario)
    }

    override fun getKardex(): List<KardexEntity> {
        return queries.getKardex().executeAsList()
    }

    override fun saveKardexMateria(materia: String, calificacion: Int?, periodo: String) {
        queries.insertKardex(materia, calificacion?.toLong(), periodo)
    }

    override fun clearAll() {
        queries.clearAllData()
    }
}