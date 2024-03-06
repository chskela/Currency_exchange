package com.currency_exchange.models

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRate(
    val id: Int,
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    val rate: Double
)
