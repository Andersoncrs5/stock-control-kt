package com.br.stock.control.util.filters.stock

import com.br.stock.control.model.entity.Stock
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface StockCustomRepository {
    fun findAll(
        productId: String?, minQuantity: Int?, maxQuantity: Int?,
        responsibleUserId: String?, warehouseId: String?, isActive: Boolean?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable
    ): Page<Stock>
}