package com.currency_exchange.routes

import com.currency_exchange.daoExchangeRates
import com.currency_exchange.utils.Constants
import com.currency_exchange.utils.Messages
import com.currency_exchange.utils.Messages.CURRENCY_PAIR_WITH_THIS_CODE_ALREADY_EXISTS
import com.currency_exchange.utils.Messages.EXCHANGE_RATE_FOR_PAIR_NOT_FOUND
import com.currency_exchange.utils.Messages.INCORRECT_VALUE_OF_RATE
import com.currency_exchange.utils.Messages.PAIR_CURRENCY_CODES_ARE_MISSING
import com.currency_exchange.utils.Messages.REQUIRED_FORM_FIELD_IS_MISSING
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.exchangeRateRoutes() {

    route("/exchangeRate") {
        gerExchangeRate()
        updateExchangeRate()
    }

    route("/exchangeRates") {
        getAllExchangeRates()
        addExchangeRate()
    }
}

private fun Route.addExchangeRate() {
    post {
        val formParameters = call.receiveParameters()
        val baseCurrencyCode = formParameters["baseCurrencyCode"]
        val targetCurrencyCode = formParameters["targetCurrencyCode"]
        val rate = formParameters["rate"]

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) return@post call.respondText(
            text = REQUIRED_FORM_FIELD_IS_MISSING,
            status = HttpStatusCode.BadRequest
        )

        try {
            daoExchangeRates.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate.toBigDecimal())
        } catch (e: Exception) {
            return@post handlerException(e)
        }?.let { exchangeRate ->
            call.respond(HttpStatusCode.Created, exchangeRate)
        }
    }
}

private fun Route.getAllExchangeRates() {
    get {
        try {
            daoExchangeRates.getAllExchangeRates()
        } catch (e: Exception) {
            return@get call.respondText(
                text = Messages.SOMETHING_WENT_WRONG,
                status = HttpStatusCode.InternalServerError
            )
        }.let { exchangeRates ->
            call.respond(HttpStatusCode.OK, exchangeRates)
        }
    }
}

private fun Route.updateExchangeRate() {
    patch("/{currencyPair}") {
        val currencyPair = call.parameters["currencyPair"] ?: return@patch call.respondText(
            text = PAIR_CURRENCY_CODES_ARE_MISSING,
            status = HttpStatusCode.BadRequest
        )

        val (baseCode, targetCode) = currencyPair.windowed(3, 3, true)
        if (currencyPair.length != 6) return@patch call.respondText(
            text = PAIR_CURRENCY_CODES_ARE_MISSING,
            status = HttpStatusCode.BadRequest
        )

        val formParameters = call.receiveParameters()
        val rate = formParameters["rate"] ?: return@patch call.respondText(
            text = REQUIRED_FORM_FIELD_IS_MISSING,
            status = HttpStatusCode.BadRequest
        )
        try {
            daoExchangeRates.updateExchangeRate(baseCode, targetCode, rate.toBigDecimal())
        } catch (e: Exception) {
            return@patch handlerException(e)

        }?.let { exchangeRate ->
            call.respond(HttpStatusCode.OK, exchangeRate)
        }
    }
}

private fun Route.gerExchangeRate() {
    get("/{currencyPair}") {
        val currencyPair = call.parameters["currencyPair"] ?: return@get call.respondText(
            text = PAIR_CURRENCY_CODES_ARE_MISSING,
            status = HttpStatusCode.BadRequest
        )
        if (currencyPair.length != 6) return@get call.respondText(
            text = PAIR_CURRENCY_CODES_ARE_MISSING,
            status = HttpStatusCode.BadRequest
        )
        val (baseCode, targetCode) = currencyPair.windowed(3, 3, true)

        val exchangeRate = daoExchangeRates
            .getExchangeRatesByCodes(baseCode, targetCode) ?: return@get call.respondText(
            text = EXCHANGE_RATE_FOR_PAIR_NOT_FOUND,
            status = HttpStatusCode.NotFound
        )

        call.respond(HttpStatusCode.OK, exchangeRate)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handlerException(e: Exception) {
    when {
        e is NumberFormatException -> call.respondText(
            text = INCORRECT_VALUE_OF_RATE,
            status = HttpStatusCode.BadRequest
        )

        e is ExposedSQLException && e.sqlState == Constants.INTEGRITY_CONSTRAINT_VIOLATION_CODE ->
            call.respondText(
                text = CURRENCY_PAIR_WITH_THIS_CODE_ALREADY_EXISTS,
                status = HttpStatusCode.Conflict
            )

        else -> call.respondText(
            text = Messages.SOMETHING_WENT_WRONG,
            status = HttpStatusCode.InternalServerError
        )
    }
}