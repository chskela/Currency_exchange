package com.currency_exchange.routes.utils

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*

fun ApplicationTestBuilder.httpClient(): HttpClient {
    environment {
        config = ApplicationConfig("application-test.conf")
    }
    return createClient {
        install(ContentNegotiation) {
            json()
        }
    }
}