package com.currency_exchange.routes

import com.currency_exchange.dao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.currenciesRoutes() {
    route("/currencies") {
        get("/{code}") {
            val code = call.parameters["code"] ?: return@get call.respondText(
                text = "Missing code",
                status = HttpStatusCode.BadRequest
            )
            val currency = dao.getCurrencyByCode(code) ?: return@get call.respondText(
                text = "No currency found",
                status = HttpStatusCode.NotFound
            )

            call.respond(currency)
        }

        get {
            call.respond(dao.getAllCurrencies())
        }

        post {

        }

        delete("/{code}") {

        }

    }
}