package com.currency_exchange.services

import com.currency_exchange.data.dao.DAOExchangeRatesFacade
import com.currency_exchange.models.Exchange
import com.currency_exchange.models.ExchangeRate
import kotlinx.coroutines.*

class ExchangeService(private val daoExchangeRatesFacade: DAOExchangeRatesFacade) {

    suspend fun getExchange(from: String, to: String, amount: Double): Exchange? = coroutineScope {

        val directExchangeRate = getDirectExchangeRate(from, to)
        val reverseExchangeRate = async(start = CoroutineStart.LAZY) { getDirectExchangeRate(to, from) }.await()
        val exchangeRateUSDToFrom = async(start = CoroutineStart.LAZY) { getExchangeRateViaUSD(from) }.await()
        val exchangeRateUSDToTo = async(start = CoroutineStart.LAZY) { getExchangeRateViaUSD(to) }.await()

        when {
            directExchangeRate != null -> Exchange(
                directExchangeRate.baseCurrency,
                directExchangeRate.targetCurrency,
                directExchangeRate.rate,
                amount,
                directExchangeRate.rate * amount
            )

            reverseExchangeRate != null -> {
                val rate = 1 / reverseExchangeRate.rate
                 Exchange(
                    reverseExchangeRate.targetCurrency,
                    reverseExchangeRate.baseCurrency,
                    rate,
                    amount,
                    amount * rate
                )
            }

            exchangeRateUSDToFrom != null && exchangeRateUSDToTo != null -> {
                val rate = exchangeRateUSDToTo.rate / exchangeRateUSDToFrom.rate
                Exchange(
                    exchangeRateUSDToFrom.targetCurrency,
                    exchangeRateUSDToTo.targetCurrency,
                    rate,
                    amount,
                    amount * rate
                )
            }

            else -> null
        }
    }

    private suspend fun getDirectExchangeRate(from: String, to: String): ExchangeRate? {
        return daoExchangeRatesFacade.getExchangeRatesByCodes(from, to)
    }

    private suspend fun getExchangeRateViaUSD(to: String): ExchangeRate? {
        return daoExchangeRatesFacade.getExchangeRatesByCodes("USD", to)
    }
}