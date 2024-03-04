package com.currency_exchange

import com.currency_exchange.data.dao.DAOFacadeImpl
import com.currency_exchange.data.dao.DatabaseSingleton
import com.currency_exchange.plugins.configureRouting
import com.currency_exchange.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val dao = DAOFacadeImpl()

fun Application.module() {
    DatabaseSingleton.init(environment.config)
    configureSerialization()
    configureRouting()
}
