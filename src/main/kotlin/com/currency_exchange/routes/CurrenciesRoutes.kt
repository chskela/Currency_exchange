package com.currency_exchange.routes

import io.ktor.server.routing.*

fun Route.currenciesRoutes() {
    route("/currencies") {
        get {

        }

        get("{code?}") {

        }

        post {

        }

        delete("{code?}") {

        }

    }
}