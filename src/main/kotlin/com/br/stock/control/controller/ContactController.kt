package com.br.stock.control.controller

import com.br.stock.control.model.dto.contact.CreateContactDTO
import com.br.stock.control.model.dto.contact.UpdateContactDTO
import com.br.stock.control.model.entity.Contact
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/contact")
class ContactController(
    private val facadeServices: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    @DeleteMapping
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit>> {
        val userId = this.facadeServices.tokenService.extractUserId(request)

        val optional = this.facadeServices.contactService.get(userId)
        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Contact not found",
                    path = request.requestURI,
                    method = request.method,
                    body = Unit
                )
            )
        }

        val contact = optional.get()

        this.facadeServices.contactService.delete(contact)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Contact deleted with successfully",
                path = request.requestURI,
                method = request.method,
                body = Unit
            )
        )
    }

    @GetMapping
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Contact?>> {
        val userId = this.facadeServices.tokenService.extractUserId(request)

        val optional = this.facadeServices.contactService.get(userId)
        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Contact not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Contact found with successfully",
                path = request.requestURI,
                method = request.method,
                body = optional.get()
            )
        )
    }

    @GetMapping("/{userId}")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        @PathVariable userId: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Contact?>> {
        val optional = this.facadeServices.contactService.get(userId)
        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Contact not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Contact found with successfully",
                path = request.requestURI,
                method = request.method,
                body = optional.get()
            )
        )
    }

    @PostMapping
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun create(
        @Valid @RequestBody dto: CreateContactDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Contact?>> {
        val userId = this.facadeServices.tokenService.extractUserId(request)

        val contactMapped = this.facadeMappers.createContactMapper.toContact(dto)
        contactMapped.userId = userId

        val contact = this.facadeServices.contactService.create(contactMapped)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Contact created with successfully",
                path = request.requestURI,
                method = request.method,
                body = contact
            )
        )
    }

    @PutMapping
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @Valid @RequestBody dto: UpdateContactDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Contact?>> {
        val userId = this.facadeServices.tokenService.extractUserId(request)
        val optional = this.facadeServices.contactService.get(userId)
        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Contact not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        val contact = optional.get()

        val contactUpdated = this.facadeServices.contactService.update(contact, dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Contact updated with successfully",
                path = request.requestURI,
                method = request.method,
                body = contactUpdated
            )
        )
    }

}