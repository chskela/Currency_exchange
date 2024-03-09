package com.currency_exchange.data.dao

import com.currency_exchange.data.dao.DatabaseSingleton.dbQuery
import com.currency_exchange.data.models.Currencies
import com.currency_exchange.data.models.ExchangeRates
import com.currency_exchange.models.Currency
import com.currency_exchange.models.ExchangeRate
import org.jetbrains.exposed.sql.*

class DAOExchangeRatesFacadeImpl : DAOExchangeRatesFacade {
    private val baseCurrency = Currencies.alias("baseCurrency")
    private val targetCurrency = Currencies.alias("targetCurrency")

    override suspend fun getAllExchangeRates(): List<ExchangeRate> = dbQuery {
        allExchangeRates().map(::resultRowToExchangeRate)
    }

    override suspend fun getExchangeRatesByCodes(baseCurrencyCode: String, targetCurrencyCode: String): ExchangeRate? =
        dbQuery {
            allExchangeRates().where {
                (ExchangeRates.baseCurrencyId eq getCurrencyIdByCode(baseCurrencyCode)) and
                        (ExchangeRates.targetCurrencyId eq getCurrencyIdByCode(targetCurrencyCode))
            }.map(::resultRowToExchangeRate).firstOrNull()
        }

    override suspend fun addExchangeRate(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double) {
        dbQuery { }
    }

    override suspend fun updateCurrency(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double) {
        dbQuery { }
    }

    private fun resultRowToExchangeRate(row: ResultRow): ExchangeRate = ExchangeRate(
        id = row[baseCurrency[Currencies.id]].value,
        baseCurrency = Currency(
            id = row[baseCurrency[Currencies.id]].value,
            code = row[baseCurrency[Currencies.code]],
            name = row[baseCurrency[Currencies.name]],
            sign = row[baseCurrency[Currencies.sign]]
        ),
        targetCurrency = Currency(
            id = row[targetCurrency[Currencies.id]].value,
            code = row[targetCurrency[Currencies.code]],
            name = row[targetCurrency[Currencies.name]],
            sign = row[targetCurrency[Currencies.sign]]
        ),
        rate = row[ExchangeRates.rate]
    )

    private fun allExchangeRates() = ExchangeRates.join(
        baseCurrency,
        JoinType.INNER,
        onColumn = ExchangeRates.baseCurrencyId,
        otherColumn = baseCurrency[Currencies.id]
    ).join(
        targetCurrency,
        JoinType.INNER,
        onColumn = ExchangeRates.targetCurrencyId,
        otherColumn = targetCurrency[Currencies.id]
    ).select(
        ExchangeRates.id,
        baseCurrency[Currencies.id],
        baseCurrency[Currencies.code],
        baseCurrency[Currencies.name],
        baseCurrency[Currencies.sign],
        targetCurrency[Currencies.id],
        targetCurrency[Currencies.code],
        targetCurrency[Currencies.name],
        targetCurrency[Currencies.sign],
        ExchangeRates.rate,
    )

    private fun getCurrencyIdByCode(currencyCode: String) = Currencies.select(Currencies.id)
        .where { Currencies.code.lowerCase() eq currencyCode.lowercase() }
        .singleOrNull()?.get(Currencies.id)?.value
}