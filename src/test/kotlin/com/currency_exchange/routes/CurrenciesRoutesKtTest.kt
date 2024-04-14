package com.currency_exchange.routes

import com.currency_exchange.data.models.Currencies
import com.currency_exchange.models.Currency
import com.currency_exchange.routes.utils.Constants.currencies
import com.currency_exchange.routes.utils.Constants.listOfTables
import com.currency_exchange.routes.utils.Constants.usd
import com.currency_exchange.routes.utils.httpClient
import com.currency_exchange.utils.Messages.CURRENCY_CODE_IS_MISSING
import com.currency_exchange.utils.Messages.CURRENCY_WITH_THIS_CODE_ALREADY_EXISTS
import com.currency_exchange.utils.Messages.NO_CURRENCY_FOUND
import com.currency_exchange.utils.Messages.REQUIRED_FORM_FIELD_IS_MISSING
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

open class CurrenciesRoutesKtTest {
    @BeforeTest
    fun setUp() {
        Database.connect("jdbc:h2:mem:fmdb;DB_CLOSE_DELAY=-1;MODE=MYSQL", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(*listOfTables)
            currencies.forEach { (_, code, name, sign) ->
                Currencies.insertIgnore {
                    it[Currencies.code] = code
                    it[Currencies.name] = name
                    it[Currencies.sign] = sign
                }
            }
        }
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(*listOfTables)
        }
    }

    @Test
    fun `get request returns all list of currencies`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/currencies")
        val list: List<Currency> = response.body()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(currencies.size, list.size)
    }

    @Test
    fun `get request returns existing currency`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/currency/USD")
        val currency: Currency = response.body()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(currencies[0].code, currency.code)
        assertEquals(currencies[0].name, currency.name)
        assertEquals(currencies[0].sign, currency.sign)
    }

    @Test
    fun `get request returns not found for not existing currency`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/currency/TES")
        val message = response.bodyAsText()
        //then
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(NO_CURRENCY_FOUND, message)
    }

    @Test
    fun `get request returns bad request for currency code missing at address`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/currency/")
        val message = response.bodyAsText()
        //then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(CURRENCY_CODE_IS_MISSING, message)
    }

    @Test
    fun `post request is adding new currency`() = testApplication {
        val client = httpClient()

        //given
        val code = "AUD"
        val name = "Australian Dollar"
        val sign = "$"

        //when
        val response = client.post("/currencies") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("code" to code, "name" to name, "sign" to sign).formUrlEncode())
        }
        val currency: Currency = response.body()

        //then
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(code, currency.code)
        assertEquals(name, currency.name)
        assertEquals(sign, currency.sign)
    }

    @Test
    fun `post request is returning bad request a required form field is missing`() = testApplication {
        val client = httpClient()

        //given
        val code = "AUD"
        val sign = "$"

        //when
        val response = client.post("/currencies") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("code" to code, "sign" to sign).formUrlEncode())
        }
        val message = response.bodyAsText()

        //then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(REQUIRED_FORM_FIELD_IS_MISSING, message)
    }

    @Test
    fun `post request is returning conflict when currency already exists`() = testApplication {
        val client = httpClient()

        //when
        val response = client.post("/currencies") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("code" to usd.code, "name" to usd.name, "sign" to usd.sign).formUrlEncode())
        }
        val message = response.bodyAsText()

        //then
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals(CURRENCY_WITH_THIS_CODE_ALREADY_EXISTS, message)
    }
}