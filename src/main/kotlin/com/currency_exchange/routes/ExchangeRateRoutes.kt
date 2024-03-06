package com.currency_exchange.routes

import com.currency_exchange.daoCurrencies
import com.currency_exchange.daoExchangeRates
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exchangeRateRoutes() {
    val pattern = Regex("""(?<baseCode>[a-zA_Z]{3})(?<targetCode>[a-zA_Z]{3})""")
    val missingCode = "Missing code"

    route("/exchangeRate/$pattern") {

        get {
            val baseCode = call.parameters["baseCode"]?: return@get call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )
            val targetCode = call.parameters["targetCode"]?: return@get call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )
        }

        patch {
            val baseCode = call.parameters["baseCode"]?: return@patch call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )
            val targetCode = call.parameters["targetCode"]?: return@patch call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )


        }
    }

    route("/exchangeRates") {
        get {
            call.respond(HttpStatusCode.OK, daoExchangeRates.getAllExchangeRates())
        }

        patch ("/{code}") {
            val code = call.parameters["code"] ?: return@patch call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )

            daoCurrencies.deleteCurrencyByCode(code)
        }
    }
}