package com.currency_exchange.data.models

import org.jetbrains.exposed.sql.Table

object Currencies : Table() {
    val id = integer("id").autoIncrement()
    val code = varchar("code", 3).uniqueIndex()
    val name = varchar("name", 1024)
    val sign = varchar("sign", 10)

    override val primaryKey = PrimaryKey(id)
}