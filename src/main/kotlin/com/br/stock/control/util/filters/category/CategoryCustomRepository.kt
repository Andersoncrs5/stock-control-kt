package com.br.stock.control.util.filters.category

import com.br.stock.control.model.entity.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface CategoryCustomRepository {
    fun filter(
        name: String?, description: String?, active: Boolean?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable
    ): Page<Category>

}