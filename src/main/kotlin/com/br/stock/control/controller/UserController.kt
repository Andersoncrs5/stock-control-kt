package com.br.stock.control.controller

import com.br.stock.control.model.dto.user.UpdateUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/user")
class UserController(
    private val facades: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    @GetMapping("me")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getUser(request: HttpServletRequest): ResponseEntity<ResponseBody<UserDTO>> {
        val userId = facades.tokenService.extractUserId(request)
        val user: User? = this.facades.userService.get(userId)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody<UserDTO>(
                    timestamp = LocalDateTime.now(),
                    message = "User not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        val dto = facadeMappers.userDTOMapper.toDTO(user)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody<UserDTO>(
                timestamp = LocalDateTime.now(),
                message = "User founded",
                path = request.requestURI,
                method = request.method,
                body = dto
            )
        )
    }

    @DeleteMapping("delete")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "deleteApiRateLimiter")
    fun delete(request: HttpServletRequest): ResponseEntity<ResponseBody<User>> {
        val userId: String = facades.tokenService.extractUserId(request)

        val user: User? = this.facades.userService.get(userId)

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

        this.facades.redisService.delete(userId)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "User deleted",
                path = request.requestURI,
                method = request.method,
                body = null
            )
        )
    }

    @GetMapping("{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getUser(@PathVariable userId: String, request: HttpServletRequest): ResponseEntity<ResponseBody<UserDTO>> {
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody<UserDTO>(
                    timestamp = LocalDateTime.now(),
                    message = "User id is required", path = request.requestURI,
                    method = request.method,body = null
                )
            )
        }

        val user: User? = this.facades.userService.get(userId)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody<UserDTO>(
                    timestamp = LocalDateTime.now(), message = "User not found",
                    path = request.requestURI, method = request.method, body = null
                )
            )
        }

        val dto = facadeMappers.userDTOMapper.toDTO(user)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody<UserDTO>(
                timestamp = LocalDateTime.now(),
                message = "User founded",
                path = request.requestURI,
                method = request.method,
                body = dto
            )
        )
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun update(@Valid @RequestBody dto: UpdateUserDTO, request: HttpServletRequest): ResponseEntity<ResponseBody<UserDTO>> {
        val userId: String = facades.tokenService.extractUserId(request)
        val user: User? = facades.userService.get(userId)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(), message = "User not found",
                    path = request.requestURI, method = request.method, body = null
                )
            )
        }

        val checkName: Boolean = this.facades.userService.existsByName(dto.name)

        if (checkName) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(), message = "Name already exists!",
                    path = request.requestURI, method = request.method, body = null
                )
            )
        }

        val mergedUser: User = facades.userService.mergeUsersData(user, dto)

        if (dto.passwordHash.isNotBlank()) { mergedUser.passwordHash = facades.cryptoService.encoderPassword(dto.passwordHash) }

        val updatedUser: User = facades.userService.updateUser(mergedUser)

        val userDTO: UserDTO = facadeMappers.userDTOMapper.toDTO(updatedUser)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(), message = "User updated",
                path = request.requestURI, method = request.method, body = userDTO
            )
        )
    }


}