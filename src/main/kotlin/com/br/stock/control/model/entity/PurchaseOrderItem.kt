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
    var quantity: Integer? = null,
    var unitPrice: BigDecimal? = null,
    var receivedQuantity: Integer? = null,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate? = null,
    @LastModifiedDate
    var updatedAt: LocalDate? = null
)
