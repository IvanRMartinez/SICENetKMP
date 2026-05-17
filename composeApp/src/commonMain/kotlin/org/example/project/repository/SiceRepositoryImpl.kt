package org.example.project.repository

import kotlinx.serialization.json.Json
import org.example.project.api.AlumnoPerfilRemote
import org.example.project.api.CargaItemRemote
import org.example.project.api.FinalItemRemote
import org.example.project.api.KardexRootRemote
import org.example.project.api.LoginResultRemote
import org.example.project.database.*
import org.example.project.api.*

class SiceRepositoryImpl(
    private val database: SiceDatabase,
    private val api: SicenetApi
) : SiceRepository {

    private val queries = database.siceDatabaseQueries

    // Configuramos el formateador JSON Multiplataforma
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // ==========================================
    // Operación Especial: LOGIN
    // ==========================================
    override suspend fun sincronizarLogin(matricula: String, contrasenia: String, tipoUsuario: String): Result<Unit> {
        return try {
            val cuerpoXml = buildLoginSoapBody(matricula, contrasenia, tipoUsuario)
            val xmlResponse = api.callSoap("http://tempuri.org/accesoLogin", cuerpoXml)

            val rawJson = xmlResponse.substringAfter("accesoLoginResult>").substringBefore("</")
            val jsonStr = SoapParser.unescapeXml(rawJson)

            val data = jsonParser.decodeFromString<LoginResultRemote>(jsonStr)

            if (data.acceso) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(data.estatus.ifBlank { "Credenciales incorrectas" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // Perfil
    // ==========================================
    override suspend fun getPerfil(): PerfilEntity? {
        return queries.getPerfil().executeAsOneOrNull()
    }

    override suspend fun savePerfil(matricula: String, nombre: String, carrera: String, ultimoAcceso: String?) {
        queries.insertPerfil(matricula, nombre, carrera, ultimoAcceso)
    }

    override suspend fun fetchRemotePerfil(): Result<PerfilEntity> {
        return try {
            val cuerpoXml = buildPerfilSoapBody()
            val xml = api.callSoap("http://tempuri.org/getAlumnoAcademicoWithLineamiento", cuerpoXml)

            val raw = SoapParser.extractTagValue(xml, "getAlumnoAcademicoWithLineamientoResult")
                ?: return Result.failure(Exception("Error en tag XML Perfil"))

            val jsonStr = SoapParser.unescapeXml(raw)
            val remote = jsonParser.decodeFromString<AlumnoPerfilRemote>(jsonStr)

            // Guardamos inmediatamente en SQLDelight
            queries.insertPerfil(
                matricula = remote.matricula,
                nombre = remote.nombre,
                carrera = remote.carrera,
                ultimoAcceso = remote.fechaReins
            )

            Result.success(queries.getPerfil().executeAsOne())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // Carga Académica
    // ==========================================
    override suspend fun getCargaAcademica(): List<CargaEntity> {
        return queries.getCarga().executeAsList()
    }

    override suspend fun saveCargaMateria(materia: String, docente: String, horario: String) {
        queries.insertCarga(materia, docente, horario)
    }

    override suspend fun fetchRemoteCarga(): Result<List<CargaEntity>> {
        return try {
            val xml = api.callSoap("http://tempuri.org/getCargaAcademicaByAlumno", buildCargaAcademicaSoapBody())
            val raw = SoapParser.extractTagValue(xml, "getCargaAcademicaByAlumnoResult") ?: ""
            val jsonStr = SoapParser.unescapeXml(raw)

            val remoteList = jsonParser.decodeFromString<List<CargaItemRemote>>(jsonStr)

            queries.transaction {
                remoteList.forEach { item ->
                    queries.insertCarga(
                        materia = item.materia,
                        docente = item.docente,
                        horario = "${item.lunes} ${item.martes} ${item.miercoles}".trim()
                    )
                }
            }
            Result.success(queries.getCarga().executeAsList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // Kardex
    // ==========================================
    override suspend fun getKardex(): List<KardexEntity> {
        return queries.getKardex().executeAsList()
    }

    override suspend fun saveKardexMateria(materia: String, calificacion: Int?, periodo: String) {
        queries.insertKardex(materia, calificacion?.toLong(), periodo)
    }

    override suspend fun fetchRemoteKardex(lineamiento: Int): Result<List<KardexEntity>> {
        return try {
            // CORREGIDO: Cambiado api.post por api.callSoap con su respectiva action url
            val xml = api.callSoap(
                "http://tempuri.org/getAllKardexConPromedioByAlumno",
                buildKardexSoapBody(lineamiento)
            )
            val raw = SoapParser.extractTagValue(xml, "getAllKardexConPromedioByAlumnoResult") ?: ""
            val jsonStr = SoapParser.unescapeXml(raw)

            val root = jsonParser.decodeFromString<KardexRootRemote>(jsonStr)

            queries.transaction {

                root.lstKardex.forEach { item ->
                    queries.insertKardex(
                        materia = item.materia,
                        calificacion = item.calificacion.toLong(),
                        periodo = "Semestre ${item.semestre}"
                    )
                }
            }
            Result.success(queries.getKardex().executeAsList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // Unidades
    // ==========================================
    override suspend fun getUnidades(): List<UnidadesEntity> {
        return queries.getUnidades().executeAsList()
    }

    override suspend fun saveUnidades(materia: String, unidades: List<Int?>, promedio: String) {
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

    override suspend fun fetchRemoteUnidades(): Result<List<UnidadesEntity>> {
        return try {
            val xml = api.callSoap("http://tempuri.org/getCalifUnidadesByAlumno", buildCalificacionesUnidadesSoapBody())
            val raw = SoapParser.extractTagValue(xml, "getCalifUnidadesByAlumnoResult") ?: ""
            val jsonStr = SoapParser.unescapeXml(raw)

            val remoteList = jsonParser.decodeFromString<List<UnidadItemRemote>>(jsonStr)

            queries.transaction {
                remoteList.forEach { item ->
                    queries.insertUnidades(
                        materia = item.materia,
                        u1 = item.c1.toLongOrNull(), u2 = item.c2.toLongOrNull(),
                        u3 = item.c3.toLongOrNull(), u4 = item.c4.toLongOrNull(),
                        u5 = item.c5.toLongOrNull(), u6 = item.c6.toLongOrNull(),
                        u7 = item.c7.toLongOrNull(), u8 = null, u9 = null, u10 = null,
                        promedio = item.promedio
                    )
                }
            }
            Result.success(queries.getUnidades().executeAsList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // Finales
    // ==========================================
    override suspend fun getFinales(): List<FinalesEntity> {
        return queries.getFinales().executeAsList()
    }

    override suspend fun saveFinal(materia: String, acreditacion: String, calificacion: Int?, observaciones: String) {
        queries.insertFinales(materia, acreditacion, calificacion?.toLong(), observaciones)
    }

    override suspend fun fetchRemoteFinales(modEducativo: Int): Result<List<FinalesEntity>> {
        return try {
            val xml = api.callSoap("http://tempuri.org/getAllCalifFinalByAlumnos", buildCalificacionesFinalesSoapBody(modEducativo))
            val raw = SoapParser.extractTagValue(xml, "getAllCalifFinalByAlumnosResult") ?: ""
            val jsonStr = SoapParser.unescapeXml(raw)

            val remoteList = jsonParser.decodeFromString<List<FinalItemRemote>>(jsonStr)

            queries.transaction {
                remoteList.forEach { item ->
                    queries.insertFinales(
                        materia = item.materia,
                        acreditacion = item.acred,
                        calificacion = item.calif.toLong(),
                        observaciones = item.observaciones
                    )
                }
            }
            Result.success(queries.getFinales().executeAsList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAll() {
        queries.clearAllData()
    }
}