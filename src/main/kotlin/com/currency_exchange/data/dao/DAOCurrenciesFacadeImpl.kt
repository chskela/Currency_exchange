package com.currency_exchange.data.dao

import com.currency_exchange.data.dao.DatabaseSingleton.dbQuery
import com.currency_exchange.data.models.Currencies
import com.currency_exchange.models.Currency
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOCurrenciesFacadeImpl : DAOCurrenciesFacade {
    override suspend fun getAllCurrencies(): List<Currency> = dbQuery {
        Currencies.selectAll().map(::resultRowToCurrency)
    }

    override suspend fun getCurrencyByCode(code: String): Currency? = dbQuery {
        Currencies.selectAll().where { Currencies.code.lowerCase() eq code.lowercase() }
            .map(::resultRowToCurrency)
            .singleOrNull()
    }

    override suspend fun addCurrency(code: String, name: String, sign: String): Currency? = dbQuery {
        val insertStatement = Currencies.insert {
            it[this.code] = code
            it[this.name] = name
            it[this.sign] = sign
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCurrency)
    }


    override suspend fun updateCurrency(currency: Currency) {
        dbQuery {
            Currencies.update({ Currencies.code eq currency.code }) {
                it[this.name] = currency.name
                it[this.sign] = currency.sign
            }
        }
    }

    override suspend fun deleteCurrencyByCode(code: String) {
        dbQuery { Currencies.deleteWhere { Currencies.code eq code } }
    }

    private fun resultRowToCurrency(row: ResultRow): Currency = Currency(
        id = row[Currencies.id].value,
        code = row[Currencies.code],
        name = row[Currencies.name],
        sign = row[Currencies.sign]
    )
}