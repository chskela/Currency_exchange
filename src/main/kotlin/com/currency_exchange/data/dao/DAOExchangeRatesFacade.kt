package com.currency_exchange.data.dao

import com.currency_exchange.models.ExchangeRate

interface DAOExchangeRatesFacade {
    suspend fun getAllExchangeRates(): List<ExchangeRate>
    suspend fun getExchangeRateById(id: Int): ExchangeRate?
    suspend fun getExchangeRatesByCodes(baseCurrencyCode: String, targetCurrencyCode: String): ExchangeRate?
    suspend fun addExchangeRate(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double): ExchangeRate?
    suspend fun updateCurrency(baseCurrencyCode: String, targetCurrencyCode: String, rate: Double)
}