package com.currency_exchange.data.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Currencies : IntIdTable() {
    val code = varchar("code", 3).uniqueIndex()
    val name = varchar("name", 200)
    val sign = varchar("sign", 10)
}