package com.br.stock.control.controller

import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/adm")
class AdmController(
    private val facadeServices: FacadeServices
) {

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
    @PostMapping("role/{name}/set/admin")
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun setRoleAdmInUser(
        @PathVariable name: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit?>> {
        if (name.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Name is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val user = this.facadeServices.userService.getUserByName(name)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "User not found with name: $name", request.requestURI,
                    request.method, null
                )
            )
        }

        val roleAdmOpt = this.facadeServices.rolesService.getByName("ROLE_ADMIN")
        if (roleAdmOpt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Role not found with name: $name", request.requestURI,
                    request.method, null
                )
            )
        }

        val roleAdmin = roleAdmOpt.get()

        this.facadeServices.rolesService.setRoleToUser(user, roleAdmin)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "User ${user.name} now is a admin!", request.requestURI,
                request.method, null
            )
        )
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
    @PostMapping("role/{name}/remove/admin")
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun removeRoleAdmInUser(
        @PathVariable name: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit?>> {
        if (name.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Name is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val user = this.facadeServices.userService.getUserByName(name)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "User not found with name: $name", request.requestURI,
                    request.method, null
                )
            )
        }

        val roleAdmOpt = this.facadeServices.rolesService.getByName("ROLE_ADMIN")
        if (roleAdmOpt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Role not found with name: $name", request.requestURI,
                    request.method, null
                )
            )
        }

        val roleAdmin = roleAdmOpt.get()

        this.facadeServices.rolesService.removeRoleFromUser(user, roleAdmin)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "User ${user.name} is not a admin!", request.requestURI,
                request.method, null
            )
        )
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PutMapping("/toggle/{name}/lock")
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun toggleLock(
        @PathVariable name: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit?>> {
        if (name.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "Name is required",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val user = this.facadeServices.userService.getUserByName(name)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "User not found with name: $name",
                    request.requestURI,
                    request.method,
                    null
                )
            )

        val userUpdated = this.facadeServices.userService.changeStatusAccountNonLocked(user)

        val msg = if (!userUpdated.accountNonLocked) "unlocked" else "locked"

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(),
                "User $name is $msg",
                request.requestURI,
                request.method,
                null
            )
        )
    }

    @GetMapping("/get-all-user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) fullName: String?,
        @RequestParam(required = false) accountNonExpired: Boolean?,
        @RequestParam(required = false) credentialsNonExpired: Boolean?,
        @RequestParam(required = false) accountNonLocked: Boolean?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<User>>> {

        val pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<User> = facadeServices.userService.getAll(
            name,
            email,
            fullName,
            accountNonExpired,
            credentialsNonExpired,
            accountNonLocked,
            createdAtBefore,
            createdAtAfter,
            pageable,
            null
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Users fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/get-all-user-with-role")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAllWithRole(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) roleName: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) fullName: String?,
        @RequestParam(required = false) accountNonExpired: Boolean?,
        @RequestParam(required = false) credentialsNonExpired: Boolean?,
        @RequestParam(required = false) accountNonLocked: Boolean?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<User>>> {

        val pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<User> = facadeServices.userService.getAll(
            name,
            email,
            fullName,
            accountNonExpired,
            credentialsNonExpired,
            accountNonLocked,
            createdAtBefore,
            createdAtAfter,
            pageable,
            roleName
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Users fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }

}