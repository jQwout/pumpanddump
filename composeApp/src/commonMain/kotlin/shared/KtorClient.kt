package shared

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val customJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    coerceInputValues = true // Заменяет null на значения по умолчанию
}

val ktorClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(customJson)
    }
}


