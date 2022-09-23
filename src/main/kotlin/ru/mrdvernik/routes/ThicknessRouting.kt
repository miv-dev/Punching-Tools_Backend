package ru.mrdvernik.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.mrdvernik.models.MetalThickness
import ru.mrdvernik.services.ThicknessService

fun Route.thicknessRouting(thicknessService: ThicknessService) {
    route("/thickness") {
        get {
            call.respond(thicknessService.all())
        }
        authenticate("access-admin") {
            post {
                val metalThickness = call.receive<MetalThickness>()
                call.respond(thicknessService.add(metalThickness))
            }
            post("/delete") {
                val ids = call.receive<List<Int>>()
                if (ids.isNotEmpty()) {
                    thicknessService.deleteCollection(ids)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }
            put {
                val metalThickness = call.receive<MetalThickness>()
                if (metalThickness.id != null) {
                    thicknessService.update(metalThickness)
                    call.respond(HttpStatusCode.OK)
                } else call.respond(HttpStatusCode.BadRequest)
            }
            get("/migrate") {
                thicknessService.migrate()
            }
        }
    }
}
