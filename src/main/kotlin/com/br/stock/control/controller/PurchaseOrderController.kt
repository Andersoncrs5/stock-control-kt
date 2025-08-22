package com.br.stock.control.controller

import com.br.stock.control.model.dto.purchaseOrder.CreateOrderDTO
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.StatusEnum
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
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@RestController
@RequestMapping("/v1/order")
class PurchaseOrderController(
    private val facades: FacadeServices,
    private val facadeMappers: FacadeMappers
) {
    private val logger = LoggerFactory.getLogger(PurchaseOrderController::class.java)

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "createApiRateLimiter")
    fun create(
        @Valid @RequestBody dto: CreateOrderDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<PurchaseOrder?>> {
        if (!this.facades.userService.existsById(dto.supplierId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "User not exists", request.requestURI,
                    request.method, null
                )
            )
        }

        val orderMapper: PurchaseOrder = this.facadeMappers.createOrderMapper.toPurchaseOrder(dto)
        orderMapper.status = StatusEnum.PENDING

        val order = this.facades.purchaseOrderService.create(orderMapper)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(), "Order created", request.requestURI,
                request.method, order
            )
        )
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseBody<PurchaseOrder?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val opt: Optional<PurchaseOrder> = this.facades.purchaseOrderService.get(id)

        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Order not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Order found", request.requestURI,
                request.method, opt.get()
            )
        )
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "deleteApiRateLimiter")
    fun delete(
        @PathVariable id: String,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseBody<Unit>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, Unit
                )
            )
        }

        val opt: Optional<PurchaseOrder> = this.facades.purchaseOrderService.get(id)

        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Order not found", request.requestURI,
                    request.method, Unit
                )
            )
        }

        val order = opt.get()

        if (order.status == StatusEnum.PROCESSING || order.status == StatusEnum.SHIPPED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Order cannot deleted with this status : ${order.status}",
                    request.requestURI, request.method, Unit
                )
            )
        }

        this.facades.purchaseOrderService.delete(order)
        this.facades.purchaseOrderItemService.deleteManyByPurchaseOrderId(order.id as String)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Order deleted", request.requestURI,
                request.method, Unit
            )
        )
    }

    @PutMapping("/{id}/status/approved")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "updateApiRateLimiter")
    fun updateStatus(
        @PathVariable id: String,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseBody<PurchaseOrder?>> {
        val userId: String = this.facades.tokenService.extractUserId(request)

        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val opt: Optional<PurchaseOrder> = this.facades.purchaseOrderService.get(id)

        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Order not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val order: PurchaseOrder = opt.get()

        val approvedOrder = this.facades.purchaseOrderService.approvedOrder(order, userId)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Status change to approved", request.requestURI,
                request.method, approvedOrder
            )
        )
    }

    @PutMapping("/{id}/status/cancel")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "updateApiRateLimiter")
    fun updateStatusCancelOrder(
        @PathVariable id: String,
        @RequestParam(required = false) reason: String?,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseBody<PurchaseOrder?>> {
        val userId: String = this.facades.tokenService.extractUserId(request)

        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val opt: Optional<PurchaseOrder> = this.facades.purchaseOrderService.get(id)

        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Order not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val order: PurchaseOrder = opt.get()

        val approvedOrder = this.facades.purchaseOrderService.cancelOrder(order, userId, reason)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Status change to cancel", request.requestURI,
                request.method, approvedOrder
            )
        )
    }

    @PutMapping("/{id}/{receiveAt}/status/receive")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "updateApiRateLimiter")
    fun updateStatusReceiveOrder(
        @PathVariable id: String,
        @PathVariable receiveAt: LocalDate,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseBody<PurchaseOrder?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val opt: Optional<PurchaseOrder> = this.facades.purchaseOrderService.get(id)

        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Order not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val order: PurchaseOrder = opt.get()

        val update = this.facades.purchaseOrderService.receiveOrder(order, receiveAt)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Status change to receive", request.requestURI,
                request.method, update
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAll(
        @RequestParam(required = false) supplierId: String?,
        @RequestParam(required = false) expectedDeliveryDateBefore: LocalDate?,
        @RequestParam(required = false) expectedDeliveryDateAfter: LocalDate?,
        @RequestParam(required = false) currency: CurrencyEnum?,
        @RequestParam(required = false) deliveryDateBefore: LocalDate?,
        @RequestParam(required = false) deliveryDateAfter: LocalDate?,
        @RequestParam(required = false) status: StatusEnum?,
        @RequestParam(required = false) receivedAtBefore: LocalDate?,
        @RequestParam(required = false) receivedAtAfter: LocalDate?,
        @RequestParam(required = false) totalAmountMin: BigDecimal?,
        @RequestParam(required = false) totalAmountMax: BigDecimal?,
        @RequestParam(required = false) shippingCostMin: BigDecimal?,
        @RequestParam(required = false) shippingCostMax: BigDecimal?,
        @RequestParam(required = false) placedByUserId: String?,
        @RequestParam(required = false) approvedByUserId: String?,
        @RequestParam(required = false) approveAtBefore: LocalDate?,
        @RequestParam(required = false) approveAtAfter: LocalDate?,
        @RequestParam(required = false) canceledByUserId: String?,
        @RequestParam(required = false) canceledAtBefore: LocalDate?,
        @RequestParam(required = false) canceledAtAfter: LocalDate?,
        @RequestParam(required = false) reasonCancel: String?,
        @RequestParam(required = false) notes: String?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<PurchaseOrder>>> {

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<PurchaseOrder> = facades.purchaseOrderService.findAll(
            supplierId, expectedDeliveryDateBefore, expectedDeliveryDateAfter, currency,
            deliveryDateBefore, deliveryDateAfter, status, receivedAtBefore, receivedAtAfter,
            totalAmountMin, totalAmountMax, shippingCostMin, shippingCostMax, placedByUserId,
            approvedByUserId, approveAtBefore, approveAtAfter, canceledByUserId, canceledAtBefore,
            canceledAtAfter, reasonCancel, notes, createdAtBefore, createdAtAfter, pageable
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Purchase Orders fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }

}