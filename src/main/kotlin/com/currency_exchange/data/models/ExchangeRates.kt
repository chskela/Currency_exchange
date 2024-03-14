package com.currency_exchange.data.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ExchangeRates : IntIdTable() {
    val baseCurrencyId = reference(
        name = "base_currency_id",
        foreign = Currencies,
        onDelete = ReferenceOption.CASCADE
    )
    val targetCurrencyId = reference(
        name = "target_currency_id",
        foreign = Currencies,
        onDelete = ReferenceOption.CASCADE
    )
    val rate = double("rate")

    init {
        uniqueIndex(baseCurrencyId, targetCurrencyId)
    }
}