package com.br.stock.control.util.filters.stockMovement

import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.model.enum.MovementTypeEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface StockMovementCustomRepository {
    fun findAll(
        stockId: String?,
        productId: String?,
        movementType: MovementTypeEnum?,
        minQuantity: Long?,
        maxQuantity: Long?,
        reason: String?,
        responsibleUserId: String?,
        notes: String?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<StockMovement>
}