package Utils

import Constantes
import Models.Usuario
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import io.ktor.server.config.*
import java.util.*

//class TokenManager (val config:HoconApplicationConfig){
class TokenManager (){
//    var secret = config.property("secret").getString()
//    var issuer = config.property("issuer").getString()
//    var audience = config.property("audience").getString()
    //        var myRealm = config.property("realm").getString()

    var secret = Constantes.secret
    var issuer = Constantes.issuer
    var audience = Constantes.audience

    fun generateJWTToken(user:Usuario):String{
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("gmail", user.gmail)
            .withClaim("admin", user.admin)
//            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))

        return token
    }

    fun verifyJWTToken() : JWTVerifier {
        return JWT.require(HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}

