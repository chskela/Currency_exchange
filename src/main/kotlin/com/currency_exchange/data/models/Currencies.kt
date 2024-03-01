package com.currency_exchange.data.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Currencies : IntIdTable() {
    val code = varchar("code", 3).uniqueIndex()
    val name = varchar("name", 200)
    val sign = varchar("sign", 10)
}

class Currency(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Currency>(Currencies)

    var code by Currencies.code
    var name by Currencies.name
    var sign by Currencies.sign
}