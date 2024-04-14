package com.currency_exchange.routes

import com.currency_exchange.daoCurrencies
import com.currency_exchange.utils.Constants.INTEGRITY_CONSTRAINT_VIOLATION_CODE
import com.currency_exchange.utils.Messages.CURRENCY_CODE_IS_MISSING
import com.currency_exchange.utils.Messages.CURRENCY_WITH_THIS_CODE_ALREADY_EXISTS
import com.currency_exchange.utils.Messages.NO_CURRENCY_FOUND
import com.currency_exchange.utils.Messages.REQUIRED_FORM_FIELD_IS_MISSING
import com.currency_exchange.utils.Messages.SOMETHING_WENT_WRONG
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.currenciesRoutes() {
    route("/currencies") {
        getAllCurrencies()
        addCurrency()
    }

    route("/currency") {
        getCurrencyByCode()
        deleteCurrencyByCode()
    }
}

private fun Route.deleteCurrencyByCode() {
    delete("/{code}") {
        val code = call.parameters["code"]
            ?: return@delete call.respondText(
                text = CURRENCY_CODE_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )

        try {
            daoCurrencies.deleteCurrencyByCode(code)
        } catch (e: Exception) {
            return@delete call.respondText(
                text = SOMETHING_WENT_WRONG,
                status = HttpStatusCode.InternalServerError
            )
        }
        daoCurrencies.deleteCurrencyByCode(code)
    }
}

private fun Route.getCurrencyByCode() {
    get("/") {
        call.respondText(
            text = CURRENCY_CODE_IS_MISSING,
            status = HttpStatusCode.BadRequest
        )
    }

    get("/{code}") {
        println("getCurrencyByCode     ${call.parameters}")
        val code = call.parameters["code"] ?: return@get call.respondText(
            text = CURRENCY_CODE_IS_MISSING,
            status = HttpStatusCode.BadRequest
        )

        if (code.length != 3) return@get call.respondText(
            text = CURRENCY_CODE_IS_MISSING,
            status = HttpStatusCode.BadRequest
        )
        try {
            daoCurrencies.getCurrencyByCode(code) ?: return@get call.respondText(
                text = NO_CURRENCY_FOUND,
                status = HttpStatusCode.NotFound
            )
        } catch (e: Exception) {
            return@get call.respondText(
                text = SOMETHING_WENT_WRONG,
                status = HttpStatusCode.InternalServerError
            )
        }.let { currency ->
            call.respond(HttpStatusCode.OK, currency)
        }
    }
}

private fun Route.getAllCurrencies() {
    get {
        try {
            daoCurrencies.getAllCurrencies()
        } catch (e: Exception) {
            return@get call.respondText(
                text = SOMETHING_WENT_WRONG,
                status = HttpStatusCode.InternalServerError
            )
        }.let { listCurrencies ->
            call.respond(HttpStatusCode.OK, listCurrencies)
        }
    }
}

private fun Route.addCurrency() {
    post {
        val formParameters = call.receiveParameters()
        val code = formParameters["code"]
        val name = formParameters["name"]
        val sign = formParameters["sign"]

        if (code == null || name == null || sign == null) {
            return@post call.respondText(
                text = REQUIRED_FORM_FIELD_IS_MISSING,
                status = HttpStatusCode.BadRequest
            )
        }

        try {
            daoCurrencies.addCurrency(code = code, name = name, sign = sign)
        } catch (e: Exception) {
            return@post if (e is ExposedSQLException && e.sqlState == INTEGRITY_CONSTRAINT_VIOLATION_CODE) {
                call.respondText(
                    text = CURRENCY_WITH_THIS_CODE_ALREADY_EXISTS,
                    status = HttpStatusCode.Conflict
                )
            } else {
                call.respondText(
                    text = SOMETHING_WENT_WRONG,
                    status = HttpStatusCode.InternalServerError
                )
            }
        }?.let { currency ->
            call.respond(HttpStatusCode.Created, currency)
        }
    }
}