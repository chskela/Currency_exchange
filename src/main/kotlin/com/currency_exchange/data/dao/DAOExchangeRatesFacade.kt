package com.currency_exchange.data.dao

import com.currency_exchange.models.Currency
import com.currency_exchange.models.ExchangeRate

interface DAOExchangeRatesFacade {
    suspend fun getAllExchangeRates(): List<ExchangeRate>
    suspend fun getExchangeRatesByCodes(baseCurrencyCode: String, targetCurrencyCode: String): ExchangeRate?
    suspend fun addExchangeRate(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double)
    suspend fun updateCurrency(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double)
}