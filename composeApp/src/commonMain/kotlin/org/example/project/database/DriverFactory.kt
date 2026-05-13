package org.example.project.database

import app.cash.sqldelight.db.SqlDriver

// Definimos la expectativa: cada plataforma debe proveer su propio Driver
expect class DriverFactory {
    fun createDriver(): SqlDriver
}

// Función de utilidad para inicializar la DB fácilmente
fun createDatabase(driverFactory: DriverFactory): SiceDatabase {
    val driver = driverFactory.createDriver()
    return SiceDatabase(driver)
}