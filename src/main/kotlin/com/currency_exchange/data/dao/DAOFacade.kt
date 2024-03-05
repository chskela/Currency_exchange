package com.currency_exchange.data.dao

import com.currency_exchange.models.Currency

interface DAOFacade {
    suspend fun getAllCurrencies(): List<Currency>
    suspend fun getCurrencyByCode(code: String): Currency?
    suspend fun addCurrency(code: String, name: String, sign: String)
    suspend fun updateCurrency(currency: Currency)
    suspend fun deleteCurrencyByCode(code: String)
}