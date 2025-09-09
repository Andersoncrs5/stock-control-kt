package com.br.stock.control.controller

import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.util.mappers.user.RegisterDTOtoUserMapper
import com.br.stock.control.util.responses.ResponseBody
import com.br.stock.control.util.responses.ResponseToken
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val facade: FacadeServices,
    private val mapper: RegisterDTOtoUserMapper,
    private val facadesMappers: FacadeMappers
) {

    @PostMapping("/register")
    @RateLimiter(name = "authSystemApiRateLimiter")
    fun register(
        @Valid @RequestBody dto: RegisterUserDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<UserDTO?>> {

        val user: User = this.mapper.toUser(dto)
        user.passwordHash = this.facade.cryptoService.encoderPassword(user.passwordHash)

        user.id = UUID.randomUUID().toString()
        user.email = user.email.lowercase().trim()
        user.accountNonLocked = false

        val saveUser: User = this.facade.userService.saveUser(user)
        val userDto: UserDTO = this.facadesMappers.userDTOMapper.toDTO(saveUser)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(),
                "User created",
                request.requestURI,
                request.method,
                userDto
            )
        )
    }

    @PostMapping("/login")
    @RateLimiter(name = "authSystemApiRateLimiter")
    fun login(
        @Valid @RequestBody dto: LoginUserDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<ResponseToken?>> {
        val user = this.facade.userService.getUserByName(dto.name)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseBody(
                    LocalDateTime.now(), "Login invalid",
                    request.requestURI, request.method, null
                )
            )
        }

        if (user.accountNonLocked) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "You are blocked",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val checkPassword: Boolean = this.facade.cryptoService.verifyPassword(dto.password, user.passwordHash)
        if (!checkPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "Login invalid",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val token: String = this.facade.tokenService.generateToken(user)
        val refreshToken: String = this.facade.tokenService.generateRefreshToken(user)

        val tokens = ResponseToken(token, refreshToken, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7))

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(),
                "Logged with successfully!",
                request.requestURI,
                request.method,
                tokens
            )
        )
    }

    @GetMapping("/refresh-token/{refreshToken}")
    @RateLimiter(name = "authSystemApiRateLimiter")
    fun refreshToken(
        @PathVariable refresh: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<ResponseToken?>> {
        if (refresh.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "Refresh token is required",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val opt: Optional<User> = this.facade.userService.getUserByRefreshToken(refresh)

        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "Unauthorized",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val user: User = opt.get()

        val token: String = this.facade.tokenService.generateToken(user)
        val refreshToken: String = this.facade.tokenService.generateRefreshToken(user)

        val tokens = ResponseToken(token, refreshToken, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7))

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(),
                "Tokens generated!",
                request.requestURI,
                request.method,
                tokens
            )
        )
    }



}