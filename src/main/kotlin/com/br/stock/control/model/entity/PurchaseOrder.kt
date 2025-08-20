package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.StatusEnum
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.math.BigDecimal
import java.time.LocalDate

data class PurchaseOrder(
    @Id
    var id: String? = null,
    var orderNumber: String = "",
    var supplierId: String = "",
    var orderDate: LocalDate? = null,
    var expectedDeliveryDate: LocalDate? = null,
    var deliveryDate: LocalDate? = null,
    var status: StatusEnum? = null,
    var totalAmount: BigDecimal? = null,
    var placedByUserId: String? = null,
    var notes: String? = null,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate? = null,
    @LastModifiedDate
    var updatedAt: LocalDate? = null,
)