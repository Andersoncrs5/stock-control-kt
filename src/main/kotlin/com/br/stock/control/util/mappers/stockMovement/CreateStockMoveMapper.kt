package com.br.stock.control.util.mappers.stockMovement

import com.br.stock.control.model.dto.stockMovement.CreateStockMoveDTO
import com.br.stock.control.model.entity.StockMovement
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateStockMoveMapper(
    private val mapper: Mapper
) {
    fun toDTO(move: StockMovement): CreateStockMoveDTO {
        return this.mapper.map(move, CreateStockMoveDTO::class.java)
    }

    fun toStockMovement(dto: CreateStockMoveDTO): StockMovement {
        return this.mapper.map(dto, StockMovement::class.java)
    }
}