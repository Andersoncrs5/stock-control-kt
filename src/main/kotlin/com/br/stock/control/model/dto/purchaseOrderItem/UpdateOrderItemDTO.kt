package com.br.stock.control.model.dto.purchaseOrderItem

import com.br.stock.control.config.annotations.customValidation.validOrderItem.OrderItem
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import java.math.BigDecimal

@OrderItem
data class UpdateOrderItemDTO(
    @field:Min(value = 1, message = "The quantity must be at least 1")
    var quantity: Int = 0,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "The unitPrice must be greater than 0")
    var unitPrice: BigDecimal = BigDecimal.valueOf(0.0),

    @field:Min(value = 0, message = "The expectedQuantity cannot be negative")
    var expectedQuantity: Int = 0,

    @field:Min(value = 0, message = "The backOrderedQuantity cannot be negative")
    var backOrderedQuantity: Int = 0,

    @field:Min(value = 0, message = "The receivedQuantity cannot be negative")
    var receivedQuantity: Int = 0,
)
