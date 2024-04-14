package com.currency_exchange.data.dao

import com.currency_exchange.models.ExchangeRate
import java.math.BigDecimal

interface DAOExchangeRatesFacade {
    suspend fun getAllExchangeRates(): List<ExchangeRate>
    suspend fun getExchangeRateById(id: Int): ExchangeRate?
    suspend fun getExchangeRatesByCodes(baseCurrencyCode: String, targetCurrencyCode: String): ExchangeRate?
    suspend fun addExchangeRate(baseCurrencyCode: String, targetCurrencyCode: String, rate: BigDecimal): ExchangeRate?
    suspend fun updateExchangeRate(
        baseCurrencyCode: String,
        targetCurrencyCode: String,
        rate: BigDecimal
    ): ExchangeRate?
}