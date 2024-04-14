package com.currency_exchange.routes

import com.currency_exchange.data.models.Currencies
import com.currency_exchange.data.models.ExchangeRates
import com.currency_exchange.models.Exchange
import com.currency_exchange.routes.utils.Constants
import com.currency_exchange.routes.utils.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExchangeRoutesKtTest {

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
    fun `get request returns direct rate exchange`() = testApplication {
        val client = httpClient()
        val amount = BigDecimal("100")
        //when
        val response = client.get("/exchange?from=usd&to=eur&amount=$amount")
        val exchange = response.body<Exchange>()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(Constants.usdeur.rate * amount, exchange.convertedAmount)
    }

    @Test
    fun `get request returns reverse rate exchange`() = testApplication {
        val client = httpClient()
        val amount = BigDecimal("100")
        //when
        val response = client.get("/exchange?from=eur&to=usd&amount=$amount")
        val exchange = response.body<Exchange>()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(amount.divide(Constants.usdeur.rate, 2, RoundingMode.CEILING), exchange.convertedAmount)
    }

    @Test
    fun `get request returns cross rate exchange`() = testApplication {
        val client = httpClient()
        val amount = BigDecimal("100")
        //when
        val response = client.get("/exchange?from=eur&to=rub&amount=$amount")
        val exchange = response.body<Exchange>()
        //then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            amount * Constants.usdrub.rate.divide(Constants.usdeur.rate, 2, RoundingMode.CEILING),
            exchange.convertedAmount
        )
    }
}