package com.currency_exchange.models

import com.currency_exchange.utils.BigDecimalAsStringSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Exchange(
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    @Serializable(with = BigDecimalAsStringSerializer::class) val rate: BigDecimal,
    @Serializable(with = BigDecimalAsStringSerializer::class) val amount: BigDecimal,
    @Serializable(with = BigDecimalAsStringSerializer::class) val convertedAmount: BigDecimal
)