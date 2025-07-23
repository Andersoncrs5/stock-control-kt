package com.br.stock.control.util.filters.products

import com.br.stock.control.model.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

interface ProductCustomRepository  {
    fun findWithFilters(name: String?, minPrice: BigDecimal?, maxPrice: BigDecimal?, pageable: Pageable): Page<Product>
}