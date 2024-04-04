package com.currency_exchange.plugins

import com.currency_exchange.routes.currenciesRoutes
import com.currency_exchange.routes.exchangeRateRoutes
import com.currency_exchange.routes.exchangeRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        currenciesRoutes()
        exchangeRateRoutes()
        exchangeRoutes()
    }
}
