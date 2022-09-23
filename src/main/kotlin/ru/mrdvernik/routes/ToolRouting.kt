package ru.mrdvernik.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ru.mrdvernik.models.GeometricType
import ru.mrdvernik.models.Tool
import ru.mrdvernik.models.ToolType
import ru.mrdvernik.services.ToolsService

@Serializable
data class GET_TOOLS(
    val tools: List<Tool> = emptyList(),
    val geometricTypes: List<GeometricType> = GeometricType.values().toList(),
    val toolTypes: List<ToolType> = ToolType.values().toList()
)

fun Route.toolRouting(toolsService: ToolsService) {
    route("/tools") {
        get {
            try {
                val tools = toolsService.all()
                call.respond(GET_TOOLS(tools))
            } catch (e: Exception) {

            }

        }
        authenticate("access-admin") {
            post {
                val tool = call.receive<Tool>()
                toolsService.create(tool)
                call.respond(HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""

                if (id.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    toolsService.delete(id.toInt())
                    call.respond(HttpStatusCode.OK)
                }
            }
            post("/delete-tools") {
                val ids = call.receive<List<Int>>()

                if (ids.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    toolsService.deleteCollection(ids)
                    call.respond(HttpStatusCode.OK)
                }

            }
            put {
                val tool = call.receive<Tool>()
                toolsService.update(tool)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun Exception.mapException(): String {


    return ""
}
