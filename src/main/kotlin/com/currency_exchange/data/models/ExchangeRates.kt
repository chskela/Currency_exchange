package com.currency_exchange.data.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ExchangeRates : IntIdTable() {
    val baseCurrencyId = reference("base_currency_id", Currencies)
    val targetCurrencyId = reference("target_currency_id", Currencies)
    val rate = double("rate")
}

class ExchangeRate(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExchangeRate>(ExchangeRates)

    var baseCurrency by Currency referencedOn ExchangeRates.baseCurrencyId
    var targetCurrency by Currency referencedOn ExchangeRates.targetCurrencyId
    var rate by ExchangeRates.rate
}
