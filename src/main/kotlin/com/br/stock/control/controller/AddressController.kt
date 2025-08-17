package com.br.stock.control.controller

import com.br.stock.control.model.dto.address.UpdateAddressDTO
import com.br.stock.control.model.entity.Address
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
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.util.Optional

@RestController
@RequestMapping("/v1/address")
class AddressController(
    private val facadeServices: FacadeServices,
    private val facadeMappers: FacadeMappers
) {
    @GetMapping("/{id}")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Address?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val save: Optional<Address> = this.facadeServices.addressService.get(id)

        if (save.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Address not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Address found", request.requestURI,
                request.method, save.get()
            )
        )
    }

    @DeleteMapping("/{id}")
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, Unit
                )
            )
        }

        val optional: Optional<Address> = this.facadeServices.addressService.get(id)

        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Address not found", request.requestURI,
                    request.method, Unit
                )
            )
        }

        this.facadeServices.addressService.delete(optional.get())

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
            ResponseBody(
                LocalDateTime.now(), "Address deleted", request.requestURI,
                request.method, Unit
            )
        )
    }

    @PutMapping("/{id}")
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody dto: UpdateAddressDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Address?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val optional: Optional<Address> = this.facadeServices.addressService.get(id)

        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Address not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val addressOriginal: Address = optional.get()
        val toMerge: Address = this.facadeMappers.updateAddressMapper.toAddress(dto)

        val update = this.facadeServices.addressService.update(addressOriginal, toMerge)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Address updated", request.requestURI,
                request.method, update
            )
        )
    }

    @PutMapping("/{id}/status/active")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun changeStatus(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Address?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val save: Optional<Address> = this.facadeServices.addressService.get(id)

        if (save.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Address not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val update = this.facadeServices.addressService.changeStatusActive(save.get())

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Address status changed", request.requestURI,
                request.method, update
            )
        )
    }


}