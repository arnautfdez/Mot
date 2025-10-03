package cat.happyband.mot.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            namingStrategy = kotlinx.serialization.json.JsonNamingStrategy.SnakeCase
        })
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("Ktor Log: $message")
            }
        }
        level = LogLevel.ALL
    }
}