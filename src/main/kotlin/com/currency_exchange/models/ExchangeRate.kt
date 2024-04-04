package com.currency_exchange.models

import com.currency_exchange.utils.BigDecimalAsStringSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class ExchangeRate(
    val id: Int,
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    @Serializable(with = BigDecimalAsStringSerializer::class) val rate: BigDecimal
)
