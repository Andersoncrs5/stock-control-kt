package com.br.stock.control.util.filters.purchaseOrder

import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.StatusEnum
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface PurchaseOrderCustomRepository {
    fun findAll(
        supplierId: String?,
        expectedDeliveryDateBefore: LocalDate?,
        expectedDeliveryDateAfter: LocalDate?,
        currency: CurrencyEnum?,
        deliveryDateBefore: LocalDate?,
        deliveryDateAfter: LocalDate?,
        status: StatusEnum?,
        receivedAtBefore: LocalDate?,
        receivedAtAfter: LocalDate?,
        totalAmount: BigDecimal?,
        shippingCost: BigDecimal?,
        placedByUserId: String?,
        approvedByUserId: String?,
        approveAtBefore: LocalDate?,
        approveAtAfter: LocalDate?,
        canceledByUserId: String?,
        canceledAtBefore: LocalDate?,
        canceledAtAfter: LocalDate?,
        reasonCancel: String?,
        notes: String?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
    ): Page<PurchaseOrder>
}