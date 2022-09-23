package ru.mrdvernik.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.mrdvernik.models.RefreshToken
import ru.mrdvernik.models.TokenPair
import ru.mrdvernik.models.User
import ru.mrdvernik.services.TokenService
import ru.mrdvernik.services.UserService

@kotlinx.serialization.Serializable
data class LoginResponse(
    val tokenPair: TokenPair,
    val user: User
)

fun Route.authRouting(userService: UserService, tokenService: TokenService) {
    route("/auth") {
        post("/refresh") {
            val oldRT = call.receive<RefreshToken>().refreshToken // old refresh token
            val token = tokenService.find(oldRT)

            val currentTime = System.currentTimeMillis()

            if (token != null && token.expiresAt > currentTime) {
                val tokenPair = tokenService.generateTokenPair(token.uuid, true)

                tokenService.updateByRefreshToken(
                    oldRT,
                    tokenPair.refreshToken,
                    currentTime
                )
                call.respond(tokenPair)
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    hashMapOf("description" to "invalid token"),
                )
            }
        }

        post("/login") {
            val authUser = call.receive<User>()

            val user = userService.userByEmail(authUser.email)

            if (user != null && authUser.password == user.password && authUser.email == user.email) {
                val tokenPair = tokenService.generateTokenPair(user.id!!)
                call.respond(
                    LoginResponse(tokenPair, user)
                )
            } else
                call.respond(HttpStatusCode.Unauthorized)
        }

    }
}
