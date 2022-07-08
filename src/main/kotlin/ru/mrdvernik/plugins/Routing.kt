package ru.mrdvernik.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import ru.mrdvernik.domain.entity.User
import ru.mrdvernik.utils.JwtProvider


fun Application.configureRouting() {

    routing {

        post("/login") {
            val user = call.receive<User>()
            // Check username and password
            // ...
            val token = JwtProvider.createJWT(user)

            call.respond(hashMapOf("token" to token ))
        }

        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
            }
        }
    }
}

