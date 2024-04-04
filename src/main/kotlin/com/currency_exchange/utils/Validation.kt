package com.currency_exchange.utils

import java.util.Currency as CurrencyUtil

object Validation {
    private val currencyCodes: Set<String> by lazy {
        CurrencyUtil.getAvailableCurrencies().map { it.currencyCode }.toSet()
    }

    fun isValidCurrencyCode(code: String) = currencyCodes.contains(code.uppercase())
}
