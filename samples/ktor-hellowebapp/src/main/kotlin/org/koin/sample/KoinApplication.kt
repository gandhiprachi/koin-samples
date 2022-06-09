package org.koin.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.modules

class HelloRepository {
    fun getHello(): String = "Ktor & Koin"
}

interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl(val helloRepository: HelloRepository) : HelloService {
    override fun sayHello() = "Hello ${helloRepository.getHello()} !"
}

fun Application.main() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)

    // Lazy inject HelloService
    val service: HelloService by inject()
    install(Koin) {
        modules(listOf(helloAppModule))
    }
    // Routing section
    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }
    }
}

val helloAppModule = module {
    single { HelloServiceImpl(get()) as HelloService }
    single { HelloRepository() }
}

fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
