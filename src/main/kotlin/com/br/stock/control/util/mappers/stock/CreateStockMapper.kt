package com.br.stock.control.util.mappers.stock

import com.br.stock.control.model.dto.stock.CreateStockDTO
import com.br.stock.control.model.entity.Stock
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateStockMapper(
    private val mapper: Mapper
) {
    fun toDTO(stock: Stock): CreateStockDTO {
        return this.mapper.map(stock, CreateStockDTO::class.java)
    }

    fun toStock(dto: CreateStockDTO): Stock {
        return this.mapper.map(dto, Stock::class.java)
    }
}