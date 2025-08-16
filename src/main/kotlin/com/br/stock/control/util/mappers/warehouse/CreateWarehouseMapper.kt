package com.br.stock.control.util.mappers.warehouse

import com.br.stock.control.model.dto.warehouse.CreateWareDTO
import com.br.stock.control.model.entity.Warehouse
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateWarehouseMapper(
    private val mapper: Mapper
) {
    fun toWarehouse(dto: CreateWareDTO): Warehouse {
        return this.mapper.map(dto, Warehouse::class.java)
    }

    fun toDTO(ware: Warehouse): CreateWareDTO {
        return this.mapper.map(ware, CreateWareDTO::class.java)
    }
}