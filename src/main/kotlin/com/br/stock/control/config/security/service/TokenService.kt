package com.br.stock.control.config.security.service

import com.br.stock.control.model.entity.User
import org.springframework.beans.factory.annotation.Value
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TokenService {

    @Value("\${spring.security.jwt.secret}")
    private val secret: String? = null

    fun generateToken(user: User): String {
        if (secret == null || secret.isBlank()) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Secret key is null");
        }

        val claimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .subject(user.email)
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(this.genExpirationDate()))
            .claim("roles", user.roles.map { it.name }.toList())
            .build();

        val header: JWSHeader = JWSHeader(JWSAlgorithm.HS256)
        val signedJWT: SignedJWT = SignedJWT(header, claimsSet);
        signedJWT.sign(MACSigner(secret.toByteArray()))

        return signedJWT.serialize();
    }

    fun generateRefreshToken(user: User): String {
        if (secret == null || secret.isBlank()) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Secret key is null");
        }

        val claimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .subject(user.email)
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
        val verifier: MACVerifier = MACVerifier(secret?.toByteArray())

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

}