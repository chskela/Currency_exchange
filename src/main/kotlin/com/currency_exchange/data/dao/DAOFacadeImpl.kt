package com.currency_exchange.data.dao

import com.currency_exchange.data.dao.DatabaseSingleton.dbQuery
import com.currency_exchange.data.models.Currencies
import com.currency_exchange.models.Currency
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll

class DAOFacadeImpl : DAOFacade {
    override suspend fun getAllCurrencies(): List<Currency> = dbQuery {
        Currencies.selectAll().map(::resultRowToArticle)
    }

    override suspend fun getCurrencyByCode(code: String): Currency? = dbQuery {
        Currencies.selectAll().where { Currencies.code.lowerCase() eq code.lowercase() }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addCurrency(currency: Currency) {
        dbQuery {  }
    }

    override suspend fun updateCurrency(currency: Currency) {
        dbQuery {  }
    }

    override suspend fun deleteCurrency(currency: Currency) {
        dbQuery {  }
    }

    private fun resultRowToArticle(row: ResultRow): Currency = Currency(
        id = row[Currencies.id].value,
        code = row[Currencies.code],
        name = row[Currencies.name],
        sign = row[Currencies.sign]
    )
}