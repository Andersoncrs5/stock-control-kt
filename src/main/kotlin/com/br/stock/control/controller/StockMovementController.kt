package com.br.stock.control.controller

import com.br.stock.control.model.dto.stockMovement.CreateStockMoveDTO
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.model.enum.MovementTypeEnum
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@RestController
@RequestMapping("/v1/stock-move")
class StockMovementController(
    private val facadeServices: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    @PostMapping
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun create(
        @Valid @RequestBody dto: CreateStockMoveDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<StockMovement?>> {
        if (!this.facadeServices.productService.existsById(dto.productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        if (!this.facadeServices.userService.existsById(dto.responsibleUserId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "User not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        val optional: Optional<Stock> = this.facadeServices.stockService.get(dto.stockId)
        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        val stock = optional.get()
        val move: StockMovement = this.facadeMappers.createStockMoveMapper.toStockMovement(dto)

        val adjustQuantity: Stock = this.facadeServices.stockService.adjustQuantity(stock, move)

        val save = this.facadeServices.stockMovementService.create(move)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(), "Stock movement created", request.requestURI,
                request.method, save
            )
        )
    }

    @GetMapping("/{id}")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<StockMovement?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val move = this.facadeServices.stockMovementService.get(id)

        if (move.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock movement not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Stock movement found", request.requestURI,
                request.method, move.get()
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

        val move = this.facadeServices.stockMovementService.get(id)

        if (move.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock movement not found", request.requestURI,
                    request.method, Unit
                )
            )
        }

        this.facadeServices.stockMovementService.delete(move.get())

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Stock movement deleted", request.requestURI,
                request.method, Unit
            )
        )
    }

    @DeleteMapping("/{ids}/many")
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun deleteMany(
        @PathVariable ids: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit>> {
        val ids = ids.split(",")

        this.facadeServices.stockMovementService.deleteMany(ids)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Stock movement many deleted", request.requestURI,
                request.method, Unit
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAll(
        @RequestParam(required = false) stockId: String?,
        @RequestParam(required = false) productId: String?,
        @RequestParam(required = false) movementType: MovementTypeEnum?,
        @RequestParam(required = false) minQuantity: Long?,
        @RequestParam(required = false) maxQuantity: Long?,
        @RequestParam(required = false) reason: String?,
        @RequestParam(required = false) responsibleUserId: String?,
        @RequestParam(required = false) notes: String?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<StockMovement>>> {

        val pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<StockMovement> = facadeServices.stockMovementService.findAll(
            stockId, productId, movementType,
            minQuantity, maxQuantity, reason,
            responsibleUserId, notes,
            createdAtBefore, createdAtAfter,
            pageable
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Stock movements fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }


}