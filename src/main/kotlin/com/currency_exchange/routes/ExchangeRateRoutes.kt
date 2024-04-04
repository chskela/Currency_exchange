package com.currency_exchange.routes

import com.currency_exchange.daoExchangeRates
import com.currency_exchange.utils.Messages.CURRENCY_PAIR_WITH_THIS_CODE_ALREADY_EXISTS
import com.currency_exchange.utils.Messages.EXCHANGE_RATE_FOR_PAIR_NOT_FOUND
import com.currency_exchange.utils.Messages.PAIR_CURRENCY_CODES_ARE_MISSING
import com.currency_exchange.utils.Messages.REQUIRED_FORM_FIELD_IS_MISSING
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.math.BigDecimal

fun Route.exchangeRateRoutes() {
    val missingCode = "Missing code"

    route("/exchangeRate") {

        get("/{currencyPair}") {
            val currencyPair = call.parameters["currencyPair"] ?: return@get call.respondText(
                text = PAIR_CURRENCY_CODES_ARE_MISSING,
                status = HttpStatusCode.BadRequest
            )

            if (currencyPair.length != 6) return@get call.respondText(
                text = PAIR_CURRENCY_CODES_ARE_MISSING,
                status = HttpStatusCode.BadRequest
            )

            val (baseCode, targetCode) = currencyPair.windowed(3, 3)

            val exchangeRate =
                daoExchangeRates.getExchangeRatesByCodes(baseCode, targetCode) ?: return@get call.respondText(
                    text = EXCHANGE_RATE_FOR_PAIR_NOT_FOUND,
                    status = HttpStatusCode.NotFound
                )

            call.respond(HttpStatusCode.OK, exchangeRate)
        }

        patch("/{currencyPair}") {
            val currencyPair = call.parameters["currencyPair"] ?: return@patch call.respondText(
                text = PAIR_CURRENCY_CODES_ARE_MISSING,
                status = HttpStatusCode.BadRequest
            )

            if (currencyPair.length != 6) return@patch call.respondText(
                text = PAIR_CURRENCY_CODES_ARE_MISSING,
                status = HttpStatusCode.BadRequest
            )

            val (baseCode, targetCode) = currencyPair.windowed(3, 3)
            val formParameters = call.receiveParameters()
            val rate = formParameters["rate"] ?: return@patch call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )
            val exchangeRate = daoExchangeRates.updateExchangeRate(baseCode, targetCode, rate.toBigDecimal())
                ?: return@patch call.respondText(
                    text = "Failed to add exchange rate",
                    status = HttpStatusCode.InternalServerError
                )
            call.respond(HttpStatusCode.OK, exchangeRate)
        }
    }

    route("/exchangeRates") {
        get {
            call.respond(HttpStatusCode.OK, daoExchangeRates.getAllExchangeRates())
        }

        post {
            val formParameters = call.receiveParameters()
            val baseCurrencyCode = formParameters["baseCurrencyCode"] ?: return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
            val targetCurrencyCode = formParameters["targetCurrencyCode"] ?: return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
            val rate = formParameters["rate"] ?: return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )

            val exchangeRate = try {
                daoExchangeRates.addExchangeRate(baseCurrencyCode, targetCurrencyCode, BigDecimal(rate))
            } catch (e: ExposedSQLException) {
                return@post call.respondText(
                    text = CURRENCY_PAIR_WITH_THIS_CODE_ALREADY_EXISTS,
                    status = HttpStatusCode.Conflict
                )
            }

            exchangeRate?.let {
                call.respond(HttpStatusCode.Created, it)
            }
        }
    }
}