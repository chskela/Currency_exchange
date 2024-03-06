package com.currency_exchange.data.dao

import com.currency_exchange.data.dao.DatabaseSingleton.dbQuery
import com.currency_exchange.data.models.Currencies
import com.currency_exchange.data.models.ExchangeRates
import com.currency_exchange.models.Currency
import com.currency_exchange.models.ExchangeRate
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.alias

class DAOExchangeRatesFacadeImpl : DAOExchangeRatesFacade {
    private val baseCurrency = Currencies.alias("baseCurrency")
    private val targetCurrency = Currencies.alias("targetCurrency")
    override suspend fun getAllExchangeRates(): List<ExchangeRate> = dbQuery {

        ExchangeRates.join(
            baseCurrency,
            JoinType.INNER,
            onColumn = ExchangeRates.baseCurrencyId,
            otherColumn = baseCurrency[Currencies.id]
        ).join(
            targetCurrency,
            JoinType.INNER,
            onColumn = ExchangeRates.targetCurrencyId,
            otherColumn = targetCurrency[Currencies.id]
        )
            .select(
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
            ).map(::resultRowToExchangeRate)
    }

    override suspend fun getExchangeRatesByCodes(baseCurrencyCode: String, targetCurrencyCode: String): ExchangeRate? {
        TODO("Not yet implemented")
    }

    override suspend fun addExchangeRate(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double) {
        dbQuery {  }
    }

    override suspend fun updateCurrency(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double) {
        dbQuery {  }
    }

    private fun resultRowToExchangeRate(row: ResultRow): ExchangeRate = ExchangeRate(
        row[ExchangeRates.id].value,
        Currency(code = row[Currencies.code], name = row[Currencies.name], sign = row[Currencies.sign]),
        Currency(row[Currencies.code], row[Currencies.name], row[Currencies.sign]),
        row[ExchangeRates.rate]
    )
}