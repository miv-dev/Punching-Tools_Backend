package ru.mrdvernik.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import ru.mrdvernik.domain.entity.User
import java.util.*

object JwtProvider {
    private const val validityInMs = 36_000_00 * 10 // 10 hours
    const val issuer = "ktor-mrdvernik"
    const val audience = "ktor-audience"

    val verifier: JWTVerifier = JWT
        .require(Cipher.algorithm)
        .withIssuer(issuer)
        .build()

    fun decodeJWT(token: String): DecodedJWT = JWT.require(Cipher.algorithm).build().verify(token)

    fun createJWT(user: User): String? =
        JWT.create()
            .withIssuedAt(Date())
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("email", user.email)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs)).sign(Cipher.algorithm)
}
