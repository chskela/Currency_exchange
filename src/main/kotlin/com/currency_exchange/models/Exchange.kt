package com.currency_exchange.models

import kotlinx.serialization.Serializable

@Serializable
data class Exchange(
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    val rate: Double,
    val amount: Double,
    val convertedAmount: Double
)