package com.currency_exchange.data.dao

import com.currency_exchange.data.models.Currency

interface DAOFacade {
    suspend fun getAllCurrencies(): List<Currency>
    suspend fun getCurrencyByCode(code: String): Currency
    suspend fun addCurrency(currency: Currency)
    suspend fun updateCurrency(currency: Currency)
    suspend fun deleteCurrency(currency: Currency)
}