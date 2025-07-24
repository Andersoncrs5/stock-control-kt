package com.br.stock.control.controller

import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadesServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import kotlin.Int
import kotlin.time.Duration

@RestController
@RequestMapping("/v1/user")
class UserController(
    private val facades: FacadesServices
) {

    @GetMapping("me")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getUser(request: HttpServletRequest): ResponseEntity<ResponseBody<User>> {
        val userId = facades.tokenService.extractUserId(request)

        var user: User? = this.facades.redisService.get<User>(userId)

        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                ResponseBody<User>(
                    timestamp = LocalDateTime.now(),
                    message = "User founded",
                    path = request.requestURI,
                    method = request.method,
                    body = user
                )
            )
        }

        user = this.facades.userService.getUser(userId)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody<User>(
                    timestamp = LocalDateTime.now(),
                    message = "User not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        this.facades.redisService.set(userId, user, java.time.Duration.ofHours(10))

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody<User>(
                timestamp = LocalDateTime.now(),
                message = "User founded",
                path = request.requestURI,
                method = request.method,
                body = user
            )
        )
    }

}