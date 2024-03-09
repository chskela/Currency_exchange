package com.currency_exchange.routes

import com.currency_exchange.daoExchangeRates
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exchangeRateRoutes() {
    val missingCode = "Missing code"

    route("/exchangeRate") {

        get(Regex("""/(?<currencyPair>[a-zA_Z]{6})""")) {
            val currencyPair = call.parameters["currencyPair"] ?: return@get call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )
            println("currencyPair: $currencyPair")
            val (baseCode, targetCode) = currencyPair.windowed(3, 3)

            println("baseCode $baseCode, targetCode: $targetCode")
            val exchangeRate =
                daoExchangeRates.getExchangeRatesByCodes(baseCode, targetCode) ?: return@get call.respondText(
                    text = "No exchange rate found",
                    status = HttpStatusCode.NotFound
                )

            call.respond(HttpStatusCode.OK, exchangeRate)
        }

        patch {

        }
    }

    route("/exchangeRates") {
        get {
            call.respond(HttpStatusCode.OK, daoExchangeRates.getAllExchangeRates())
        }

        post {

        }
    }
}