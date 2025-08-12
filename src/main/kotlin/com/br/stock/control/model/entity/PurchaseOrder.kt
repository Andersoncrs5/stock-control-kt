package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.StatusEnum
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.math.BigDecimal
import java.time.LocalDateTime

data class PurchaseOrder(
    @Id
    var id: String,
    var orderNumber: String,
    var supplier: String,
    var orderDate: LocalDateTime,
    var expectedDeliveryDate: LocalDateTime,
    var deliveryDate: LocalDateTime,
    var status: StatusEnum,
    var totalAmount: BigDecimal,
    var placedByUserId: String,
    var notes: String,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)