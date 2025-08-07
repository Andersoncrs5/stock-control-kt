package com.br.stock.control.util.mappers.product

import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.entity.Product
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateProductMapper(
    private val mapper: Mapper
) {
    fun toDTO(product: Product): CreateProductDTO {
        return this.mapper.map(product, CreateProductDTO::class.java)
    }

    fun toProduct(dto: CreateProductDTO): Product {
        return this.mapper.map(dto, Product::class.java)
    }
}