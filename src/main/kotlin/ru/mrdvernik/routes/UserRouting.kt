package ru.mrdvernik.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.postgresql.util.PSQLException
import ru.mrdvernik.models.User
import ru.mrdvernik.services.UserService
import java.util.*

fun Route.userRouting(userService: UserService) {

    route("/users") {
        authenticate("access") {
            get("/current") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asString()
                userService.userById(userId)?.let {
                    call.respond(it)
                }
            }
        }
        authenticate("access-admin") {
            post {
                val user = call.receive<User>()
                userService.create(user)
                call.respond(HttpStatusCode.OK)
            }
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asString()

                call.respond(userService.users(UUID.fromString(userId)))
            }
            put {
                val user = call.receive<User>()
                if (user.id != null) {
                   try {
                       userService.update(user)
                       call.respond(HttpStatusCode.OK)
                   } catch (e: PSQLException){
                       call.respond(HttpStatusCode.BadRequest, hashMapOf("error" to e.message))
                   }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post(
                "/delete"
            ) {
                val uuids = call.receive<List<UUID>>()
            }
        }
    }
}
