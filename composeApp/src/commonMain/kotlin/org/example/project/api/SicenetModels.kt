package org.example.project.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AlumnoPerfilRemote(
    val matricula: String = "",
    val nombre: String = "",
    val carrera: String = "",
    val semActual: Int = 0,
    val cdtosActuales: Int = 0,
    val cdtosAcumulados: Int = 0,
    val especialidad: String = "",
    val estatus: String = "",
    val inscrito: Boolean = false,
    val urlFoto: String = "",
    val modEducativo: Int = 0,
    val fechaReins: String = "",
    val adeudo: Boolean = false,
    val adeudoDescripcion: String = "",
    val lineamiento: Int = 0
)

@Serializable
data class LoginResultRemote(
    val acceso: Boolean = false,
    val estatus: String = ""
)

@Serializable
data class KardexRootRemote(
    val lstKardex: List<KardexItemRemote> = emptyList()
)

@Serializable
data class KardexItemRemote(
    @SerialName("ClvMat") val clvMatOld: String? = null,
    @SerialName("clvMat") val clvMatNew: String? = null,
    @SerialName("Materia") val materiaOld: String? = null,
    @SerialName("matName") val matNameNew: String? = null,
    @SerialName("Calif") val califOld: Int? = null,
    @SerialName("calif") val califNew: Int? = null,
    @SerialName("Acreditacion") val acreditacionOld: String? = null,
    @SerialName("acreditacion") val acreditacionNew: String? = null,
    @SerialName("Semestre") val semestreOld: Int? = null,
    @SerialName("semestre") val semestreNew: Int? = null
) {
    // Selectores rápidos para resolver la inconsistencia de nombres de la escuela
    val clave get() = clvMatOld ?: clvMatNew ?: ""
    val materia get() = materiaOld ?: matNameNew ?: ""
    val calificacion get() = califOld ?: califNew ?: 0
    val acreditacion get() = acreditacionOld ?: acreditacionNew ?: ""
    val semestre get() = semestreOld ?: semestreNew ?: 0
}

@Serializable
data class CargaItemRemote(
    val clvOficial: String = "",
    @SerialName("Materia") val materia: String = "",
    @SerialName("Docente") val docente: String = "",
    @SerialName("CreditosMateria") val creditosMateria: Int = 0,
    @SerialName("Lunes") val lunes: String = "",
    @SerialName("Martes") val martes: String = "",
    @SerialName("Miercoles") val miercoles: String = "",
    @SerialName("Jueves") val jueves: String = "",
    @SerialName("Viernes") val viernes: String = "",
    @SerialName("Sabado") val sabado: String = ""
)

@Serializable
data class UnidadItemRemote(
    @SerialName("Materia") val materia: String = "",
    @SerialName("C1") val c1: String = "",
    @SerialName("C2") val c2: String = "",
    @SerialName("C3") val c3: String = "",
    @SerialName("C4") val c4: String = "",
    @SerialName("C5") val c5: String = "",
    @SerialName("C6") val c6: String = "",
    @SerialName("C7") val c7: String = "",
    @SerialName("Promedio") val promedio: String = ""
)

@Serializable
data class FinalItemRemote(
    @SerialName("Materia") val materia: String = "",
    @SerialName("Calif") val calif: Int = 0,
    @SerialName("Acred") val acred: String = "",
    @SerialName("Observaciones") val observaciones: String = ""
)