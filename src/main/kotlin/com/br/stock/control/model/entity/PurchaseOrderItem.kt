package com.br.stock.control.model.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.math.BigDecimal
import java.time.LocalDateTime

data class PurchaseOrderItem(
    @Id
    var id: ObjectId,
    var purchaseOrder: ObjectId,
    var product: ObjectId,
    var quantity: Integer,
    var unitPrice: BigDecimal,
    var subtotal: BigDecimal,
    var receivedQuantity: Integer,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
