package com.br.stock.control.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.math.BigDecimal
import java.time.LocalDate

data class PurchaseOrderItem(
    @Id
    var id: String? = null,
    var purchaseOrderId: String,
    var productId: String = "",
    var quantity: Int = 0,
    var unitPrice: BigDecimal = BigDecimal.valueOf(0.0),
    var expectedQuantity: Int = 0,
    var backOrderedQuantity: Int = 0,
    var receivedQuantity: Int = 0,
    @Version
    var version: Long = 0,
    var createdAt: LocalDate? = null,
    @LastModifiedDate
    var updatedAt: LocalDate? = null
)
