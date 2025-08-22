package com.br.stock.control.util.mappers.purchaseOrder

import com.br.stock.control.model.dto.purchaseOrder.CreateOrderDTO
import com.br.stock.control.model.entity.PurchaseOrder
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateOrderMapper(
    private val mapper: Mapper
) {
    fun toDTO(order: PurchaseOrder): CreateOrderDTO {
        return this.mapper.map(order, CreateOrderDTO::class.java)
    }

    fun toPurchaseOrder(dto: CreateOrderDTO): PurchaseOrder {
        return this.mapper.map(dto, PurchaseOrder::class.java)
    }
}