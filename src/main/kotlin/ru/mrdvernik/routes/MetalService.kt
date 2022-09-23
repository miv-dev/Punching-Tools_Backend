package ru.mrdvernik.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.mrdvernik.models.Delta
import ru.mrdvernik.models.Metal
import ru.mrdvernik.services.MetalService

@kotlinx.serialization.Serializable
data class DeleteMetalDelta(
    val idList: List<String>
)

fun Route.metalRouting(metalService: MetalService = MetalService()) {
    route("/metal") {
        post {
            val metal = call.receive<Metal>()
            try {
                metalService.addMetal(metal)
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }
        get {
            call.respond(metalService.all())
        }

        put {
            val metal = call.receive<Metal>()
            try {
                metalService.updateMetal(metal)
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }
        route("/{id}/delta") {
            post {
                val id = call.parameters["id"]
                val delta = call.receive<Delta>()
                metalService.addDelta(id!!.toInt(), delta).fold(
                    {
                        call.respond(HttpStatusCode.OK)
                    }, {
                        call.respond(
                            hashMapOf(
                                "status" to "error",
                                "msg" to it.message
                            )
                        )
                    }
                )
            }
            delete {
                val id = call.parameters["id"]
                val deleteMetalDelta = call.receive<DeleteMetalDelta>()

                if (id != null) {

                    metalService.deleteDelta(id.toInt(), deleteMetalDelta)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                }


            }
        }
    }
}
