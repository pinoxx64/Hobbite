package Rutes

import Controllers.UsuarioController
import Models.Login
import Models.Usuario
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

    route("/admin") {

        get("/usuarios/{id}"){
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

        get("/usuarios") {
            val principal = call.principal<JWTPrincipal>()
            var gmail = principal?.payload?.getClaim("gmail")?.asString()
            var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
            var usu = Login(gmail,contrasena)
            var admin = UsuarioController.admin(usu)

            if (admin) {
                try{
                    val usuarios = UsuarioController.getUsuarios()
                    call.respond(HttpStatusCode.OK, usuarios)

                }catch (e:Exception){
                    call.respond(HttpStatusCode.BadRequest, e)
                }

            } else {
                call.respond(HttpStatusCode.Forbidden, mapOf("mensaje" to "Acceso no autorizado. Se requieren permisos de administrador."))
            }
        }

        post("/usuarios") {
            val principal = call.principal<JWTPrincipal>()
            var gmail = principal?.payload?.getClaim("gmail")?.asString()
            var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
            var usu = Login(gmail,contrasena)
            var admin = UsuarioController.admin(usu)

            if (admin) {
                try {
                    val usuario = call.receive<Usuario>()
                    val usuarioRegistrado = UsuarioController.postUsuario(usuario)

                    if (usuarioRegistrado) {
                        call.respond(HttpStatusCode.Created, mapOf("mensaje" to "Usuario registrado correctamente"))
                    } else {
                        call.respond(HttpStatusCode.Conflict, mapOf("mensaje" to "El usuario ya existe"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Error en la solicitud"))
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, mapOf("mensaje" to "Acceso no autorizado. Se requieren permisos de administrador."))
            }
        }

        put("/usuarios/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val gmail = principal?.payload?.getClaim("gmail")?.asString()
            val contrasena = principal?.payload?.getClaim("contrasena")?.asString()
            val usu = Login(gmail, contrasena)
            val admin = UsuarioController.admin(usu)

            if (admin) {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id != null) {
                        val usuario = call.receive<Usuario>()
                        val actualizado = UsuarioController.updateUsuario(id, usuario)

                        if (actualizado) {
                            call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Usuario actualizado correctamente"))
                        } else {
                            call.respond(HttpStatusCode.NotFound, mapOf("mensaje" to "Usuario no encontrado"))
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "ID de usuario inválido"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e)
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, mapOf("mensaje" to "Acceso no autorizado. Se requieren permisos de administrador."))
            }
        }

        delete("/usuarios/{id}") {
            val principal = call.principal<JWTPrincipal>()
            var gmail = principal?.payload?.getClaim("gmail")?.asString()
            var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
            var usu = Login(gmail, contrasena)
            var admin = UsuarioController.admin(usu)

            if (admin) {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()

                    if (id != null) {
                        val eliminado = UsuarioController.deleteUsuario(id)

                        if (eliminado) {
                            call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Usuario eliminado correctamente"))
                        } else {
                            call.respond(HttpStatusCode.NotFound, mapOf("mensaje" to "Usuario no encontrado"))
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "ID de usuario inválido"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Error en la solicitud"))
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, mapOf("mensaje" to "Acceso no autorizado. Se requieren permisos de administrador."))
            }
        }
    }

    route("/registrar") {
        post{
            try {
                val usuario = call.receive<Usuario>()
                val usuarioRegistrado = UsuarioController.postUsuario(usuario)

                if (usuarioRegistrado) {
                    call.respond(HttpStatusCode.Created, usuarioRegistrado)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e)
            }
        }
    }

    route("/login") {
        post{
            try {
                val usuario = call.receive<Usuario>()
                val autenticado = UsuarioController.login(usuario)

                if (autenticado) {
                    val token = tokenManager.generateJWTToken(usuario)
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Credenciales incorrectas")
                }
            } catch (e: Exception) {
                println("Error al recibir la solicitud: $e")
                call.respond(HttpStatusCode.BadRequest, "Error de deserialización")
            }
        }
    }

    route("/juego"){
        authenticate{
            post("crear"){
                val principal = call.principal<JWTPrincipal>()
                var gmail = principal?.payload?.getClaim("gmail")?.asString()
                var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
                var usu = Login(gmail,contrasena)
                var idUsuario = UsuarioController.getIdUsuarioLogeado(usu)

                var idPartida = PartidaControladorGeneral.iniciarPartida(20,idUsuario)

                if (idPartida) {
                    call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Partida iniciada exitosamente"))

                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("mensaje" to "Error al iniciar la partida"))
                }
            }

            get("partidas"){
                val principal = call.principal<JWTPrincipal>()
                var gmail = principal?.payload?.getClaim("gmail")?.asString()
                var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
                var usu = Login(gmail,contrasena)
                var idUsuario = UsuarioController.getIdUsuarioLogeado(usu)

                val partidas = PartidaControlador.obtenerPartidas(idUsuario)
                val responseMap = mapOf("partidas" to partidas)
                call.respond(HttpStatusCode.OK, responseMap)
            }

            post("/jugar/{id}/{casilla}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    var gmail = principal?.payload?.getClaim("gmail")?.asString()
                    var contrasena = principal?.payload?.getClaim("contrasena")?.asString()
                    var usu = Login(gmail,contrasena)
                    var idUsuario = UsuarioController.getIdUsuarioLogeado(usu)

                    val idPartida = call.parameters["id"]?.toIntOrNull()
                    val casilla = call.parameters["casilla"]?.toIntOrNull()

                    if (idPartida != null && casilla != null) {
                        val tienePartida = PartidaControlador.tienePartida(idUsuario, idPartida)


                        if (tienePartida) {
                            val resultado = PartidaControladorGeneral.jugar(idPartida, casilla)

                            val resultadoJson = Json.encodeToString(ResultadoPartida(resultado))

                            call.respond(HttpStatusCode.OK, resultadoJson)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "El usuario no tiene una partida con ese número"))
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "ID de partida o casilla no válido"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, e)
                }
            }
        }
    }
}