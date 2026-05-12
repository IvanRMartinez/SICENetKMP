package org.example.project
import org.example.project.database.SiceDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform