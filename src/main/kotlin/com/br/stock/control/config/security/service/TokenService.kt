package com.br.stock.control.config.security.service

import com.br.stock.control.model.entity.User
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class TokenService {

    private val logger = LoggerFactory.getLogger(TokenService::class.java)

    @Value("\${spring.security.jwt.secret}")
    private val secret: String? = null

    fun generateToken(user: User): String {
        logger.debug("Generating token...")
        if (secret == null || secret.isBlank()) {
            logger.error("secret key came null")
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Secret key is null")
        }

        logger.debug("Setting claims to token..")
        val claimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .subject(user.email)
            .claim("userId", user.id)
            .claim("name", user.name)
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(this.genExpirationDate()))
            .claim("roles", user.roles.map { it.name }.toList())
            .build()

        val header: JWSHeader = JWSHeader(JWSAlgorithm.HS256)
        val signedJWT: SignedJWT = SignedJWT(header, claimsSet)
        signedJWT.sign(MACSigner(secret.toByteArray()))

        logger.debug("generated token")
        return signedJWT.serialize()
    }

    fun generateRefreshToken(user: User): String {
        logger.debug("Generating refreshToken...")
        if (secret == null || secret.isBlank()) {
            logger.error("Error the generate refreshToken! secret came null")
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Secret key is null");
        }


        val claimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .subject(user.email)
            .claim("userId", user.id)
            .claim("name", user.name)
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(this.genExpirationDateRefreshToken()))
            .claim("roles", user.roles.map { it.name }.toList())
            .build();

        val header: JWSHeader = JWSHeader(JWSAlgorithm.HS256)
        val signedJWT: SignedJWT = SignedJWT(header, claimsSet);
        signedJWT.sign(MACSigner(secret.toByteArray()))

        return signedJWT.serialize();
    }

    fun validateToken(token: String): String {
        if (token.isBlank())
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val signedJWT: SignedJWT = SignedJWT.parse(token)
        val verifier = MACVerifier(secret?.toByteArray())

        if (!signedJWT.verify(verifier)) {  throw ResponseStatusException(HttpStatus.UNAUTHORIZED)  }

        val claimSet: JWTClaimsSet = signedJWT.jwtClaimsSet;
        if (claimSet.expirationTime.before(Date.from(Instant.now()))) { throw ResponseStatusException(HttpStatus.UNAUTHORIZED) }

        return claimSet.subject
    }

    private fun genExpirationDate(): Instant {
        return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-03:00"))
    }

    private fun genExpirationDateRefreshToken(): Instant {
        return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"))
    }

    fun extractAllClaims(token: String): Map<String, Any> {
        val signedJWT: SignedJWT = SignedJWT.parse(token)
        val claimSet: JWTClaimsSet = signedJWT.jwtClaimsSet

        return claimSet.claims
    }

    fun extractSubjectToken(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization")
        val token = authHeader.substring(7)

        val signedJWT: SignedJWT = SignedJWT.parse(token)
        val claimSet: JWTClaimsSet = signedJWT.jwtClaimsSet

        return claimSet.subject
    }

    fun extractUserId(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization")
        val token = authHeader.substring(7)

        val signedJWT: SignedJWT = SignedJWT.parse(token)
        val claimSet: JWTClaimsSet = signedJWT.jwtClaimsSet

        return claimSet.claims.getValue("userId").toString()
    }

    fun extractName(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization")
        val token = authHeader.substring(7)

        val signedJWT: SignedJWT = SignedJWT.parse(token)
        val claimSet: JWTClaimsSet = signedJWT.jwtClaimsSet

        return claimSet.claims.getValue("name").toString()
    }

}