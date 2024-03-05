package com.currency_exchange.routes

import com.currency_exchange.dao
import com.currency_exchange.models.Currency
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.currenciesRoutes() {
    route("/currencies") {
        get {
            call.respond(HttpStatusCode.OK, dao.getAllCurrencies())
        }

        post {
            val currency = call.receiveNullable<Currency>() ?: return@post call.respondText(
                text = "Missing code",
                status = HttpStatusCode.BadRequest
            )
            // TODO: handler 409
            dao.addCurrency(code = currency.code, name = currency.name, sign = currency.sign)
        }
    }

    route("/currency") {
        get("/{code}") {
            val code = call.parameters["code"] ?: return@get call.respondText(
                text = "Missing code",
                status = HttpStatusCode.BadRequest
            )
            val currency = dao.getCurrencyByCode(code) ?: return@get call.respondText(
                text = "No currency found",
                status = HttpStatusCode.NotFound
            )

            call.respond(HttpStatusCode.OK, currency)
        }


        delete("/{code}") {
            val code = call.parameters["code"] ?: return@delete call.respondText(
                text = "Missing code",
                status = HttpStatusCode.BadRequest
            )

            dao.deleteCurrencyByCode(code)
        }

    }
}