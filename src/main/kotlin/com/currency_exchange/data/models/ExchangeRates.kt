package com.currency_exchange.data.models

import org.jetbrains.exposed.dao.id.IntIdTable

object ExchangeRates : IntIdTable() {
    val baseCurrencyId = reference("base_currency_id", Currencies)
    val targetCurrencyId = reference("target_currency_id", Currencies)
    val rate = double("rate")
}