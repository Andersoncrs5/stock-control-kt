package com.br.stock.control.model.dto.purchaseOrderItem

import java.math.BigDecimal

data class CreateOrderItemDTO(
    var productId: String = "",
    var quantity: Int = 0,
    var unitPrice: BigDecimal = BigDecimal.valueOf(0.0),
    var expectedQuantity: Int = 0,
    var backOrderedQuantity: Int = 0,
    var receivedQuantity: Int = 0,
)
