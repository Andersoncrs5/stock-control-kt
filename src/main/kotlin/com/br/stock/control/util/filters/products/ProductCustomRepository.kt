package com.br.stock.control.util.filters.products

import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

interface ProductCustomRepository  {
    fun findWithFilters(name: String?,sku: String?,barcode: String?,categoryId: String?,unitOfMeasure: UnitOfMeasureEnum?, minPrice: BigDecimal?, maxPrice: BigDecimal?, minCost: BigDecimal?,maxCost: BigDecimal?,isActive: Boolean?, createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable): Page<Product>
}