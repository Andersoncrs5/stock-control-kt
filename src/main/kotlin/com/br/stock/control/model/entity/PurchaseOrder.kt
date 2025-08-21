package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.CurrencyEnum
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
    var supplierId: String = "",
    var expectedDeliveryDate: LocalDate? = null,
    var currency: CurrencyEnum = CurrencyEnum.NONE ,
    var deliveryDate: LocalDate? = null,

    var status: StatusEnum = StatusEnum.NONE,
    var receivedAt: LocalDate? = null,

    var totalAmount: BigDecimal = BigDecimal.valueOf(0.0),
    var shippingCost: BigDecimal = BigDecimal.valueOf(0.0),
    var placedByUserId: String? = null,

    var approvedByUserId: String? = null,
    var approveAt: LocalDate? = null,

    var canceledByUserId: String? = null,
    var canceledAt: LocalDate? = null,
    var reasonCancel: String? = null,

    var notes: String? = null,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate? = null,
    @LastModifiedDate
    var updatedAt: LocalDate? = null,
)