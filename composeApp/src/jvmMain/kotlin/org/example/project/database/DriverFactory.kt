package org.example.project.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        // Definimos la ruta del archivo en el sistema
        val databasePath = File(System.getProperty("java.io.tmpdir"), "sicedroid.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")

        // A diferencia de Android, aquí hay que crear el esquema manualmente si el archivo es nuevo
        if (!databasePath.exists()) {
            SiceDatabase.Schema.create(driver)
        }

        return driver
    }
}