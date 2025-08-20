package com.br.stock.control.controller

import com.br.stock.control.model.dto.supplier.CreateSupplierDTO
import com.br.stock.control.model.dto.supplier.UpdateSupplierDTO
import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.entity.User
import com.br.stock.control.model.enum.SupplierStatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/supplier")
class SupplierController(
    private val services: FacadeServices,
    private val mappers: FacadeMappers
) {
    @PostMapping
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun create(
        @Valid @RequestBody dto: CreateSupplierDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Supplier?>> {
        val supplierMapped: Supplier = this.mappers.createSupplierMapper.toSupplier(dto)

        if(this.services.supplierService.existsByCnpj(supplierMapped.cnpj as String)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Cnpj exists",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        val userId: String = services.tokenService.extractUserId(request)
        val user: User? = this.services.userService.get(userId)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "User not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        supplierMapped.userId = user.id
        supplierMapped.status = SupplierStatusEnum.ACTIVE

        for (i: String in supplierMapped.categoriesId) {
            if (!this.services.category.existsById(i)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseBody(
                        timestamp = LocalDateTime.now(),
                        message = "Category not exists",
                        path = request.requestURI,
                        method = request.method,
                        body = null
                    )
                )
            }
        }

        val supplier: Supplier = this.services.supplierService.save(supplierMapped)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Supplier created",
                path = request.requestURI,
                method = request.method,
                body = supplier
            )
        )
    }

    @GetMapping("/me")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Supplier?>> {
        val userId: String = services.tokenService.extractUserId(request)

        val opt = this.services.supplierService.get(userId)
        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Supplier not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Supplier found",
                path = request.requestURI,
                method = request.method,
                body = opt.get()
            )
        )
    }

    @DeleteMapping
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit>> {
        val userId: String = services.tokenService.extractUserId(request)

        val opt = this.services.supplierService.get(userId)
        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Supplier not found",
                    path = request.requestURI,
                    method = request.method,
                    body = Unit
                )
            )
        }

        this.services.supplierService.delete(opt.get())

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Supplier deleted",
                path = request.requestURI,
                method = request.method,
                body = Unit
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAllSuppliers(
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) cnpj: String?,
        @RequestParam(required = false) nameEnterprise: String?,
        @RequestParam(required = false) notes: String?,
        @RequestParam(required = false) status: SupplierStatusEnum?,
        @RequestParam(required = false) type: SupplierTypeEnum?,
        @RequestParam(required = false) minRating: Int?,
        @RequestParam(required = false) maxRating: Int?,
        @RequestParam(required = false) categoriesId: List<String>?,
        @RequestParam(required = false) createdBy: String?,
        @RequestParam(required = false) isPreferred: Boolean?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<Supplier>>> {

        val pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<Supplier> = services.supplierService.findAll(
            userId, cnpj, nameEnterprise, notes, status, type,
            minRating, maxRating, categoriesId, createdBy,
            isPreferred, createdAtBefore, createdAtAfter, pageable
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Suppliers fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }

    @PutMapping
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @Valid @RequestBody dto: UpdateSupplierDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Supplier?>> {
        val userId: String = services.tokenService.extractUserId(request)

        if(this.services.supplierService.existsByCnpj(dto.cnpj)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Cnpj exists",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        val opt = this.services.supplierService.get(userId)
        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(),
                    message = "Supplier not found",
                    path = request.requestURI,
                    method = request.method,
                    body = null
                )
            )
        }

        val supplier: Supplier = opt.get()

        val update: Supplier = this.services.supplierService.update(supplier, dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Supplier updated",
                path = request.requestURI,
                method = request.method,
                body = update
            )
        )
    }

}