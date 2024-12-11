package Rutes

import Controllers.UsuarioController
import Models.Login
import Utils.TokenManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.userRoutes() {
    val tokenManager = TokenManager()

    route("/usuario/{id}"){
        val principal = call.principal<JWTPrincipal>()
        var gmail = principal?.payload?.getClaim("gmail")?.asString()
        var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
        var usuario = Login(gmail, contrasena)
        var admin = UsuarioController.admin(usuario)

        if (admin) {
            try {
                val id = call.parameters["id"]?.toInt()
                val usuario = UsuarioController.getUsuarioPorId(id)

                if (usuario != null) {
                    call.respond(HttpStatusCode.OK, usuario)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("mensaje" to "Usuario no encontrado"))
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Error en la solicitud"))
            }
        }else{
            call.respond(HttpStatusCode.Forbidden, mapOf("mensaje" to "Acceso no autorizado. Se requieren permisos de administrador."))
        }
    }
}