package com.currency_exchange.routes

import com.currency_exchange.data.models.Currencies
import com.currency_exchange.data.models.ExchangeRates
import com.currency_exchange.models.ExchangeRate
import com.currency_exchange.routes.utils.Constants
import com.currency_exchange.routes.utils.Constants.eur
import com.currency_exchange.routes.utils.Constants.rub
import com.currency_exchange.routes.utils.httpClient
import com.currency_exchange.utils.Messages.CURRENCY_PAIR_WITH_THIS_CODE_ALREADY_EXISTS
import com.currency_exchange.utils.Messages.EXCHANGE_RATE_FOR_PAIR_NOT_FOUND
import com.currency_exchange.utils.Messages.PAIR_CURRENCY_CODES_ARE_MISSING
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
import java.math.BigDecimal
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

open class ExchangeRateRoutesKtTest {

    @BeforeTest
    fun setUp() {
        Database.connect("jdbc:h2:mem:fmdb;DB_CLOSE_DELAY=-1;MODE=MYSQL", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(*Constants.listOfTables)
            Constants.currencies.forEach { (_, code, name, sign) ->
                Currencies.insertIgnore {
                    it[Currencies.code] = code
                    it[Currencies.name] = name
                    it[Currencies.sign] = sign
                }
            }
            Constants.exchangeRates.forEach { (_, baseCurrency, targetCurrency, rate) ->
                val baseCurrencyId = Currencies.select(Currencies.id).where { Currencies.code eq baseCurrency.code }
                    .first()[Currencies.id]
                val targetCurrencyId = Currencies.select(Currencies.id).where { Currencies.code eq targetCurrency.code }
                    .first()[Currencies.id]
                ExchangeRates.insertIgnore {
                    it[ExchangeRates.baseCurrencyId] = baseCurrencyId.value
                    it[ExchangeRates.targetCurrencyId] = targetCurrencyId.value
                    it[ExchangeRates.rate] = rate
                }
            }
        }
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(*Constants.listOfTables)
        }
    }

    @Test
    fun `get request returns all list of exchangeRates`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/exchangeRates")
        val list: List<ExchangeRate> = response.body()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(Constants.exchangeRates.size, list.size)
    }

    @Test
    fun `get request returns existing exchange rate`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/exchangeRate/usdrub")
        val exchangeRate: ExchangeRate = response.body()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(Constants.usdrub.baseCurrency.code, exchangeRate.baseCurrency.code)
        assertEquals(Constants.usdrub.targetCurrency.code, exchangeRate.targetCurrency.code)
        assertEquals(Constants.usdrub.rate, exchangeRate.rate)
    }

    @Test
    fun `get request returns bad request the pair's currency codes are missing in the address`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/exchangeRate/eur")
        val message = response.bodyAsText()
        //then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(PAIR_CURRENCY_CODES_ARE_MISSING, message)
    }

    @Test
    fun `get request returns not found for exchange rate for pair not found`() = testApplication {
        val client = httpClient()

        //when
        val response = client.get("/exchangeRate/eurrub")
        val message = response.bodyAsText()
        //then
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(EXCHANGE_RATE_FOR_PAIR_NOT_FOUND, message)
    }

    @Test
    fun `post request adding a new exchange rate to the database`() = testApplication {
        val client = httpClient()
        //given
        val rate = BigDecimal("1.30")
        //when
        val response = client.post("/exchangeRates") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(
                listOf(
                    "baseCurrencyCode" to eur.code,
                    "targetCurrencyCode" to rub.code,
                    "rate" to "$rate"
                ).formUrlEncode()
            )
        }
        val exchangeRate: ExchangeRate = response.body()
        //then
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(eur.code, exchangeRate.baseCurrency.code)
        assertEquals(rub.code, exchangeRate.targetCurrency.code)
        assertEquals(rate, exchangeRate.rate)
    }

    @Test
    fun `post request is returning bad request a required form field is missing`() = testApplication {
        val client = httpClient()

        //when
        val response = client.post("/exchangeRates") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(
                listOf(
                    "baseCurrencyCode" to eur.code,
                    "targetCurrencyCode" to rub.code,
                ).formUrlEncode()
            )
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
        val response = client.post("/exchangeRates") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(
                listOf(
                    "baseCurrencyCode" to Constants.usdeur.baseCurrency.code,
                    "targetCurrencyCode" to Constants.usdeur.targetCurrency.code,
                    "rate" to "${Constants.usdeur.rate}"
                ).formUrlEncode()
            )

        }
        val message = response.bodyAsText()

        //then
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals(CURRENCY_PAIR_WITH_THIS_CODE_ALREADY_EXISTS, message)
    }


}
