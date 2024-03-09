package com.currency_exchange.models

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val id: Int,
    val code: String,
    val name: String,
    val sign: String
)
