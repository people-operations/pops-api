package pops.infraestructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pops.domain.model.entity.Person
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class TokenService(
    @Value("\${jwt.secret}")
    private var secret: String,
    private val ISSUER: String = "pops-srv-manager-api"
) {
    fun generateToken(person: Person): String{
        try {
            val algorithm: Algorithm = Algorithm.HMAC256(secret)

            val token: String = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(person.email)
                .withExpiresAt(this.generateExpirationDate())
                .sign(algorithm)
            return token
        } catch (e: JWTCreationException){
            throw RuntimeException("Error while authenticating");
        }
    }

    fun validateToken(token: String?): String? {
        try {
            val algorithm = Algorithm.HMAC256(secret)
            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .subject
        } catch (exception: JWTVerificationException) {
            return null
        }
    }

    private fun generateExpirationDate(): Instant {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"))
    }
}
