package com.br.stock.control.util.filters.warehouse

import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.WareHouseEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WareHouseCustomRepository {
    fun findWithFilters(
        name: String?,
        description: String?,
        minAmount: Long?,
        maxAmount: Long?,
        minCubicMeters: Double?,
        maxCubicMeters: Double?,
        type: WareHouseEnum?,
        isActive: Boolean?,
        canToAdd: Boolean?,
        createdAtBefore: LocalDateTime?,
        createdAtAfter: LocalDateTime?,
        pageable: Pageable): Page<Warehouse>
}