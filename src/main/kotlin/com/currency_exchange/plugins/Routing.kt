package com.currency_exchange.plugins

import com.currency_exchange.routes.currenciesRoutes
import com.currency_exchange.routes.exchangeRateRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        currenciesRoutes()
        exchangeRateRoutes()
    }
}
