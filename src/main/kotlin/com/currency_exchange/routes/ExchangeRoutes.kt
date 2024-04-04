package com.currency_exchange.routes

import com.currency_exchange.data.dao.DAOExchangeRatesFacadeImpl
import com.currency_exchange.services.ExchangeService
import com.currency_exchange.utils.Messages.CURRENCY_NOT_FOUND
import com.currency_exchange.utils.Messages.REQUIRED_FORM_FIELD_IS_MISSING
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exchangeRoutes() {
    route("/exchange") {

        get {
            val from = call.request.queryParameters["from"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                REQUIRED_FORM_FIELD_IS_MISSING
            )
            val to = call.request.queryParameters["to"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                REQUIRED_FORM_FIELD_IS_MISSING
            )
            val amount = call.request.queryParameters["amount"]?.toBigDecimalOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, REQUIRED_FORM_FIELD_IS_MISSING
            )
            val exchange = ExchangeService(DAOExchangeRatesFacadeImpl()).getExchange(from, to, amount)
                ?: return@get call.respond(HttpStatusCode.NotFound, CURRENCY_NOT_FOUND)

            call.respond(HttpStatusCode.OK, exchange)

        }
    }
}
