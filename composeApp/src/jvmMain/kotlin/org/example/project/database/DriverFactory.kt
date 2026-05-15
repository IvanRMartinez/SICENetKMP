package org.example.project.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

class DesktopDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "sicedroid.db")

        // 1. Preguntamos SI ES NUEVA antes de que el driver toque el archivo
        val isNewDatabase = !databasePath.exists()

        // 2. Ahora sí, creamos la conexión (esto podría crear el archivo en blanco)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")

        // 3. Usamos la variable que guardamos en el paso 1
        if (isNewDatabase) {
            SiceDatabase.Schema.create(driver)
        }

        return driver
    }
}