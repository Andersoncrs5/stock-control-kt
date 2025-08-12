package com.br.stock.control.util.mappers.category

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.github.dozermapper.core.Mapper
import com.br.stock.control.model.entity.Category
import org.springframework.stereotype.Service

@Service
class CreateCategoryMapper(
    private val mapper: Mapper
) {
    fun toDTO(category: Category): CreateCategoryDTO {
        return mapper.map(category, CreateCategoryDTO::class.java)
    }

    fun toCategory(dto: CreateCategoryDTO): Category {
        return mapper.map(dto, Category::class.java)
    }
}