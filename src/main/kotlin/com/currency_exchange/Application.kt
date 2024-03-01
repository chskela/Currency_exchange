package com.currency_exchange

import com.currency_exchange.data.dao.DatabaseSingleton
import com.currency_exchange.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseSingleton.init(environment.config)
    configureSerialization()
    configureRouting()
}
