package com.br.stock.control.util.filters.purchaseOrderItem

import com.br.stock.control.model.entity.PurchaseOrderItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

interface PurchaseOrderItemCustomRepository {
    fun findAll(
        purchaseOrderId: String?,
        productId: String?,
        minQuantity: Int?,
        maxQuantity: Int?,
        minExpectedQuantity: Int?,
        maxExpectedQuantity: Int?,
        minBackOrderedQuantity: Int?,
        maxBackOrderedQuantity: Int?,
        minReceivedQuantity: Int?,
        maxReceivedQuantity: Int?,
        minUnitPrice: BigDecimal?,
        maxUnitPrice: BigDecimal?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<PurchaseOrderItem>
}