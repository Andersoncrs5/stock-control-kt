package com.br.stock.control.controller

import com.br.stock.control.model.dto.purchaseOrderItem.CreateOrderItemDTO
import com.br.stock.control.model.dto.purchaseOrderItem.UpdateOrderItemDTO
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.entity.PurchaseOrderItem
import com.br.stock.control.model.enum.StatusEnum
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
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
import java.util.UUID

@RestController
@RequestMapping("/v1/order-item")
class PurchaseOrderItemController(
    private val facadeServices: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    @GetMapping("/{id}")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<PurchaseOrderItem?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val save: Optional<PurchaseOrderItem> = this.facadeServices.purchaseOrderItemService.get(id)
        if (save.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Purchase Order Item not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "PurchaseOrderItem found", request.requestURI,
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

        val save: Optional<PurchaseOrderItem> = this.facadeServices.purchaseOrderItemService.get(id)
        if (save.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Purchase Order Item not found", request.requestURI,
                    request.method, Unit
                )
            )
        }

        val optOrder = this.facadeServices.purchaseOrderService.get(save.get().purchaseOrderId)
        if (optOrder.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Purchase Order not found", request.requestURI,
                    request.method, Unit
                )
            )
        }

        if (
            optOrder.get().status == StatusEnum.PENDING ||
            optOrder.get().status == StatusEnum.CANCELLED ||
            optOrder.get().status == StatusEnum.APPROVED ||
            optOrder.get().status == StatusEnum.NONE ||
            optOrder.get().status == StatusEnum.RECEIVED
            ) {
            this.facadeServices.purchaseOrderItemService.delete(save.get())

            return ResponseEntity.status(HttpStatus.OK).body(
                ResponseBody(
                    LocalDateTime.now(), "PurchaseOrderItem deleted", request.requestURI,
                    request.method, Unit
                )
            )
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ResponseBody(
                LocalDateTime.now(), "Cannot delete order items!", request.requestURI,
                request.method, Unit
            )
        )
    }

    @PostMapping("/{purchaseOrderId}")
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun save(
        @PathVariable(required = true) purchaseOrderId: String,
        @Valid @RequestBody dto: CreateOrderItemDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<PurchaseOrderItem?>> {
        if (purchaseOrderId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val userId = this.facadeServices.tokenService.extractUserId(request)

        val opt: Optional<PurchaseOrder> = this.facadeServices.purchaseOrderService.get(purchaseOrderId)
        if (opt.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Purchase Order not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val order: PurchaseOrder = opt.get()
        if (order.status != StatusEnum.PENDING) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseBody(
                    LocalDateTime.now(), "Cannot add any more order items to the order.", request.requestURI,
                    request.method, null
                )
            )
        }

        if (order.supplierId == userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseBody(
                    LocalDateTime.now(), "A supplier cannot place an order with your company", request.requestURI,
                    request.method, null
                )
            )
        }

        val optProduct: Product? = this.facadeServices.productService.get(dto.productId)
        if (optProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val orderMapped: PurchaseOrderItem = this.facadeMappers.createPurchaseOrderItemMapper.toPurchaseOrderItem(dto)

        orderMapped.productId = optProduct.id
        orderMapped.purchaseOrderId = purchaseOrderId
        order.id = UUID.randomUUID().toString()

        val orderItemSave: PurchaseOrderItem = this.facadeServices.purchaseOrderItemService.create(orderMapped)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(), "Order item created", request.requestURI,
                request.method, orderItemSave
            )
        )
    }

    @PutMapping("/{id}")
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody dto: UpdateOrderItemDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<PurchaseOrderItem?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "Id is required",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val save: Optional<PurchaseOrderItem> = this.facadeServices.purchaseOrderItemService.get(id)
        if (save.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(),
                    "Purchase Order Item not found",
                    request.requestURI,
                    request.method,
                    null
                )
            )
        }

        val orderItem = save.get()

        val update: PurchaseOrderItem = this.facadeServices.purchaseOrderItemService.update(orderItem, dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(),
                "Purchase Order Item updated with successfully!",
                request.requestURI,
                request.method,
                update
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun findAll(
        @RequestParam(required = false) purchaseOrderId: String?,
        @RequestParam(required = false) productId: String?,
        @RequestParam(required = false) minQuantity: Int?,
        @RequestParam(required = false) maxQuantity: Int?,
        @RequestParam(required = false) minExpectedQuantity: Int?,
        @RequestParam(required = false) maxExpectedQuantity: Int?,
        @RequestParam(required = false) minBackOrderedQuantity: Int?,
        @RequestParam(required = false) maxBackOrderedQuantity: Int?,
        @RequestParam(required = false) minReceivedQuantity: Int?,
        @RequestParam(required = false) maxReceivedQuantity: Int?,
        @RequestParam(required = false) minUnitPrice: BigDecimal?,
        @RequestParam(required = false) maxUnitPrice: BigDecimal?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<PurchaseOrderItem>>> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<PurchaseOrderItem> = facadeServices.purchaseOrderItemService.findAll(
            purchaseOrderId,
            productId,
            minQuantity,
            maxQuantity,
            minExpectedQuantity,
            maxExpectedQuantity,
            minBackOrderedQuantity,
            maxBackOrderedQuantity,
            minReceivedQuantity,
            maxReceivedQuantity,
            minUnitPrice,
            maxUnitPrice,
            createdAtBefore,
            createdAtAfter,
            pageable
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Purchase order items fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }

}