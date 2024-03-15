package com.currency_exchange.routes

import com.currency_exchange.daoCurrencies
import com.currency_exchange.utils.Messages.CURRENCY_CODE_IS_MISSING
import com.currency_exchange.utils.Messages.CURRENCY_WITH_THIS_CODE_ALREADY_EXISTS
import com.currency_exchange.utils.Messages.NO_CURRENCY_FOUND
import com.currency_exchange.utils.Messages.REQUIRED_FORM_FIELD_IS_MISSING
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Route.currenciesRoutes() {
    val missingCode = "Missing code"
    route("/currencies") {
        get {
            call.respond(HttpStatusCode.OK, daoCurrencies.getAllCurrencies())
        }

        post {
            val formParameters = call.receiveParameters()
            val code = formParameters["code"] ?: return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
            val name = formParameters["name"] ?: return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
            val sign = formParameters["sign"] ?: return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )

            val currency = try {
                daoCurrencies.addCurrency(code = code, name = name, sign = sign)
            } catch (e: ExposedSQLException) {
                return@post call.respondText(
                    text = CURRENCY_WITH_THIS_CODE_ALREADY_EXISTS,
                    status = HttpStatusCode.Conflict
                )
            }
            currency?.let {
                call.respond(HttpStatusCode.Created, it)
            }
        }
    }

    route("/currency") {
        get("/{code}") {
            val code = call.parameters["code"] ?: return@get call.respondText(
                text = CURRENCY_CODE_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
            if (code.length != 3) return@get call.respondText(
                text = CURRENCY_CODE_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
            val currency = daoCurrencies.getCurrencyByCode(code) ?: return@get call.respondText(
                text = NO_CURRENCY_FOUND,
                status = HttpStatusCode.NotFound
            )

            call.respond(HttpStatusCode.OK, currency)
        }

        delete("/{code}") {
            val code = call.parameters["code"] ?: return@delete call.respondText(
                text = missingCode,
                status = HttpStatusCode.BadRequest
            )

            daoCurrencies.deleteCurrencyByCode(code)
        }
    }
}