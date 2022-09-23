package ru.mrdvernik.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import ru.mrdvernik.models.GeometricType
import ru.mrdvernik.models.MetalName
import ru.mrdvernik.models.MetalThickness
import ru.mrdvernik.services.*
import java.io.File
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart


@Serializable
data class GET_GENERAL(
    val thicknessList: List<MetalThickness>,
    val metalNames: List<MetalName>,
    val geometricTypes: List<GeometricType>,
)

@Serializable
data class GetAvailableTools(
    val thicknessId: Int,
    val metalId: Int,
    val geometricType: GeometricType
)

@Serializable
data class POSTCreateOrder(
    val msg: String,
)


fun Route.generalRouting(
    metalService: MetalService,
    toolsService: ToolsService,
    thicknessService: ThicknessService,
    generalService: GeneralService = GeneralService(),
    userService: UserService
) {
    route("/general") {
        get {
            val metalNames = metalService.allNames()
            val thicknessList = thicknessService.all()
            val geometricTypes = GeometricType.values().toList()
            call.respond(
                GET_GENERAL(
                    thicknessList,
                    metalNames,
                    geometricTypes
                )
            )

        }
        post("/available-tools") {
            val filters = call.receive<GetAvailableTools>()
            val response = generalService.availableTools(filters)

            call.respond(response)
        }
        authenticate("access") {
            post("/create-order") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asString()
                val user = userService.userById(userId)

                val multipart = call.receiveMultipart()
                var msg = ""
                var filename: String? = null
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            msg = part.value
                            println(msg)
                        }

                        is PartData.FileItem -> {
                            if (filename == null) {
                                filename = part.originalFileName as String
                                part.streamProvider().readBytes().also {
                                    File("uploads/$filename").writeBytes(it)
                                }
                                println(filename)
                            }
                        }

                        else -> {}
                    }
                }
                if (filename != null) {
                    val multipart = MimeMultipart()

                    val messageBodyPart = MimeBodyPart()
                    messageBodyPart.setContent(msg, "text/html; charset=\"UTF-8\"")
                    multipart.addBodyPart(messageBodyPart)


                    val fileBodyPart = MimeBodyPart()

                    val attachFile = "uploads/$filename"
                    val source: DataSource = FileDataSource(attachFile)
                    fileBodyPart.dataHandler = DataHandler(source)
                    fileBodyPart.fileName = File(attachFile).name
                    fileBodyPart.disposition = MimeBodyPart.ATTACHMENT
                    withContext(Dispatchers.IO) {
                        fileBodyPart.attachFile(File(attachFile))
                    }
                    multipart.addBodyPart(fileBodyPart)


                    SimpleEmail().apply {
                        hostName = "smtp.mail.ru"
                        setSmtpPort(465)
                        setAuthenticator(DefaultAuthenticator("i@mike39.ru", "wvrUNYXimSEeauvwhiRp"))
                        isSSLOnConnect = true
                        setFrom("i@mike39.ru")
                        addTo(user.email)
                        setContent(multipart)
                        send()
                    }
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }
        }
    }
}
