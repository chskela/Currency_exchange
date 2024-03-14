package com.currency_exchange.data.dao

import com.currency_exchange.models.Currency

interface DAOCurrenciesFacade {
    suspend fun getAllCurrencies(): List<Currency>
    suspend fun getCurrencyByCode(code: String): Currency?
    suspend fun addCurrency(code: String, name: String, sign: String): Currency?
    suspend fun updateCurrency(currency: Currency)
    suspend fun deleteCurrencyByCode(code: String)
}