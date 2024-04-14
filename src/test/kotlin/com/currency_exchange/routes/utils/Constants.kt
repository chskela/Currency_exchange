package com.currency_exchange.routes.utils

import com.currency_exchange.data.models.Currencies
import com.currency_exchange.data.models.ExchangeRates
import com.currency_exchange.models.Currency
import com.currency_exchange.models.ExchangeRate
import java.math.BigDecimal

object Constants {
    val usd = Currency(1, "USD", "United States dollar", "$")
    val eur = Currency(2, "EUR", "Euro Member Countries", "€")
    val rub = Currency(3, "RUB", "Russia Ruble", "₽")
    val currencies = listOf(usd, eur, rub)
    val usdeur = ExchangeRate(1, usd, eur, BigDecimal("1.10"))
    val usdrub = ExchangeRate(1, usd, rub, BigDecimal("0.90"))
    val exchangeRates = listOf(usdeur, usdrub)

    val listOfTables = arrayOf(Currencies, ExchangeRates)
}