package com.br.stock.control.util.mappers.category

import com.br.stock.control.model.dto.category.UpdateCategoryDTO
import com.br.stock.control.model.entity.Category
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class UpdateCategoryMapper(
    private val mapper: Mapper
) {
    fun toDTO(category: Category): UpdateCategoryDTO {
        return mapper.map(category, UpdateCategoryDTO::class.java)
    }

    fun toCategory(dto: UpdateCategoryDTO): Category {
        return mapper.map(dto, Category::class.java)
    }
}