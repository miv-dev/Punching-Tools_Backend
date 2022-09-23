package ru.mrdvernik

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.mrdvernik.config.DbConfig
import ru.mrdvernik.routes.*
import ru.mrdvernik.services.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.stringProperty(path: String): String =
    this.environment.config.property(path).getString()

fun Application.longProperty(path: String): Long =
    stringProperty(path).toLong()


@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    environment.config.apply {
        val dbUrl = property("db.dbUrl").getString()
        val username = property("db.username").getString()
        val password = property("db.password").getString()

        DbConfig.setup(dbUrl, username, password)
    }


    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
        allowCredentials = true
    }
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    val tokenService = TokenService(
        stringProperty("jwt.issuer"),
        Algorithm.HMAC256(stringProperty("jwt.access.secret")),
        longProperty("jwt.access.lifetime"),
        longProperty("jwt.refresh.lifetime")
    )
    val userService = UserService()
    val toolsService = ToolsService()
    val metalService = MetalService()
    val thicknessService = ThicknessService()
    install(Authentication) {
        jwt("access") {
            verifier {
                tokenService.makeJWTVerifier()
            }

            validate { token ->
                if (token.payload.expiresAt.time > System.currentTimeMillis())
                    JWTPrincipal(token.payload)
                else null
            }
        }
        jwt("access-admin") {
            verifier {
                tokenService.makeJWTVerifier()
            }

            validate { token ->

                val userId = token.payload.getClaim(
                    "userId"
                ).asString()
                println(userId)
                if (token.payload.expiresAt.time > System.currentTimeMillis() && userService.userById(
                        userId
                    ).isAdmin
                ) {
                    JWTPrincipal(token.payload)
                } else null
            }

        }
    }
    routing {
        route("/api") {
            thicknessRouting(thicknessService)
            authRouting(userService, tokenService)
            userRouting(userService)
            metalRouting(metalService)
            toolRouting(toolsService)
            generalRouting(metalService, toolsService, thicknessService, GeneralService(), userService)
        }
    }
}
