package ru.mrdvernik

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.callloging.*
import ru.mrdvernik.plugins.*
import ru.mrdvernik.utils.JwtProvider

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {


    install(Authentication){
        jwt {
            verifier(JwtProvider.verifier)
            authSchemes("Token")
            validate { credential ->
                if (credential.payload.audience.contains(JwtProvider.audience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    install(CallLogging)

    configureRouting()
    configureSerialization()
}
