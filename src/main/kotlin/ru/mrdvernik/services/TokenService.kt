package ru.mrdvernik.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mrdvernik.db.entities.TokenEntity
import ru.mrdvernik.db.tables.TokensTable
import ru.mrdvernik.models.TokenPair
import ru.mrdvernik.utils.withOffset
import java.time.Duration
import java.util.*



class TokenService(
    private val issuer: String,
    private val algorithm: Algorithm,
    private val accessLifeTime: Long,
    private val refreshLifetime: Long
) {
    init {
        transaction {
            SchemaUtils.create(TokensTable)
        }
    }

    fun makeJWTVerifier(): JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    suspend fun find(refreshToken: String) = newSuspendedTransaction {
        TokenEntity.find { TokensTable.refreshToken eq refreshToken }
            .map { it.toDomain() }.singleOrNull()

    }

    suspend fun generateTokenPair(userId: UUID, isUpdate: Boolean = false): TokenPair = newSuspendedTransaction {
        val currentTime = System.currentTimeMillis()

        val accessToken = JWT.create()
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(accessLifeTime))))
            .withIssuer(issuer)
            .sign(algorithm)
        val refreshToken = UUID.randomUUID().toString()

        if (!isUpdate) {
            TokenEntity.new {
                this.userId = userId
                this.refreshToken = refreshToken
                this.expiresAt = currentTime.withOffset(Duration.ofDays(refreshLifetime))
            }
        }
        TokenPair(accessToken, refreshToken)
    }


    suspend fun updateByRefreshToken(oldRT: String, newRT: String, currentTime: Long) = newSuspendedTransaction {
        TokenEntity.find { TokensTable.refreshToken eq oldRT }.first().apply {
            this.expiresAt = currentTime.withOffset(Duration.ofDays(refreshLifetime))
            this.refreshToken = newRT
        }
    }
}
