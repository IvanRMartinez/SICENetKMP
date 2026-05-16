package org.example.project.api

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets

class SicenetApi(private val client: HttpClient) {

    // Cambia esto por la URL base real del servidor del Sicenet de tu escuela
    private val baseUrl = "https://sicenet.tuinstituto.edu.mx"

    suspend fun callSoap(
        soapAction: String,
        bodyXml: String
    ): String {
        // Hacemos la petición POST apuntando al archivo .asmx en el servidor
        val response = client.post("$baseUrl/wsalumnos.asmx") {
            // Reemplazamos los @Header de Retrofit
            header("SOAPAction", soapAction)
            header("Accept", "text/xml")

            // Reemplazamos el @Body de Retrofit enviando el XML como texto crudo
            contentType(ContentType.Text.Xml.withCharset(Charsets.UTF_8))
            setBody(bodyXml)
        }

        // Ktor nos devuelve el XML de respuesta como un String completo
        return response.bodyAsText()
    }
}