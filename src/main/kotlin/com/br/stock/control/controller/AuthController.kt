package com.br.stock.control.controller

import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.mappers.user.LoginUserDTO
import com.br.stock.control.util.mappers.user.RegisterDTOtoUserMapper
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val facade: FacadeServices,
    private val mapper: RegisterDTOtoUserMapper
) {

    @PostMapping("/register")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "authSystemApiRateLimiter")
    fun register(@Valid @RequestBody dto: RegisterUserDTO, request: HttpServletRequest): ResponseEntity<ResponseBody<User>?> {
        val existsByEmail = this.facade.userService.existsByEmail(dto.email)

        if (existsByEmail) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    LocalDateTime.now(), "Email already exists!",
                    request.requestURI, request.method, null
                )
            )
        }

        val user: User = this.mapper.toUser(dto)

        user.passwordHash = this.facade.cryptoService.cryptoPassword(user.passwordHash)

        val saveUser: User = this.facade.userService.saveUser(user)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(),
                "User created",
                request.requestURI,
                request.method,
                saveUser
            )
        )
    }

    @PostMapping("/login")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "authSystemApiRateLimiter")
    fun login(@Valid @RequestBody dto: LoginUserDTO, request: HttpServletRequest)/*: ResponseEntity<ResponseBody<Any>>*/ {
        val user = this.facade.userService.getUserByName(dto.name)
    }

}