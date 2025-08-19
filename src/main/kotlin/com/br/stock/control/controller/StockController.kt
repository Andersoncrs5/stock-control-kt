package com.br.stock.control.controller

import com.br.stock.control.model.dto.stock.CreateStockDTO
import com.br.stock.control.model.dto.stock.UpdateStockDTO
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
@RequestMapping("/v1/stock")
class StockController(
    private val facades: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    private val logger = LoggerFactory.getLogger(StockController::class.java)

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "createApiRateLimiter")
    fun create(
        @Valid @RequestBody dto: CreateStockDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Stock?>> {
        if (!this.facades.productService.existsById(dto.productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        if (!this.facades.wareHouseService.existsById(dto.warehouseId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        val userId: String = facades.tokenService.extractUserId(request)
        val user: User? = this.facades.userService.get(userId)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(), message = "User not found",
                    path = request.requestURI, method = request.method, body = null
                )
            )
        }

        val stock: Stock = this.facadeMappers.createStockMapper.toStock(dto)

        val save: Stock = this.facades.stockService.create(stock)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(), "Stock created", request.requestURI,
                request.method, save
            )
        )
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Stock?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val stock = this.facades.stockService.get(id)

        if (stock.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Stock found", request.requestURI,
                request.method, stock.get()
            )
        )
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun delete(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Unit>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "id is required", request.requestURI,
                    request.method, Unit
                )
            )
        }

        val stock = this.facades.stockService.get(id)

        if (stock.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock not found", request.requestURI,
                    request.method, Unit
                )
            )
        }

        this.facades.stockService.delete(stock.get())
        this.facades.stockMovementService.deleteManyByStockId(stock.get().id as String)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Stock deleted", request.requestURI,
                request.method, Unit
            )
        )
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "updateApiRateLimiter")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody dto: UpdateStockDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Stock?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val optional = this.facades.stockService.get(id)

        if (optional.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val stock: Stock = optional.get()

        if (!this.facades.wareHouseService.existsById(dto.warehouseId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        val userId: String = facades.tokenService.extractUserId(request)
        val user: User? = this.facades.userService.get(userId)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    timestamp = LocalDateTime.now(), message = "User not found",
                    path = request.requestURI, method = request.method, body = null
                )
            )
        }

        val update = this.facades.stockService.update(stock, dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(), message = "Stock updated",
                path = request.requestURI, method = request.method, body = update
            )
        )
    }

    @PutMapping("/{id}/status/active")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun changeStatus(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Stock?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val stock = this.facades.stockService.get(id)

        if (stock.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Stock not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val changeStatus = this.facades.stockService.changeStatus(stock.get())

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Status changed", request.requestURI,
                request.method, changeStatus
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAllStocks(
        @RequestParam(required = false) productId: String?,
        @RequestParam(required = false) minQuantity: Int?,
        @RequestParam(required = false) maxQuantity: Int?,
        @RequestParam(required = false) responsibleUserId: String?,
        @RequestParam(required = false) warehouseId: String?,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<Stock>>> {

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<Stock> = facades.stockService.findAll(
            productId, minQuantity, maxQuantity,
            responsibleUserId, warehouseId, isActive,
            createdAtBefore, createdAtAfter, pageable
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Stocks fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }


}