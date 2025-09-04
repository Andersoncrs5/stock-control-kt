package com.br.stock.control.util.mappers.purchaseOrderItem

import com.br.stock.control.model.dto.purchaseOrderItem.CreateOrderItemDTO
import com.br.stock.control.model.entity.PurchaseOrderItem
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreatePurchaseOrderItemMapper(
    private val mapper: Mapper
) {
    fun toDTO(item: PurchaseOrderItem): CreateOrderItemDTO {
        return this.mapper.map(item, CreateOrderItemDTO::class.java)
    }

    fun toPurchaseOrderItem(dto: CreateOrderItemDTO): PurchaseOrderItem {
        return this.mapper.map(dto, PurchaseOrderItem::class.java)
    }
}