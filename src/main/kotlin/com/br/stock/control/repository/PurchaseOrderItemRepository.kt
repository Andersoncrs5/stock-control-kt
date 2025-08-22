package com.br.stock.control.repository

import com.br.stock.control.model.entity.PurchaseOrderItem
import org.springframework.data.mongodb.repository.MongoRepository

interface PurchaseOrderItemRepository: MongoRepository<PurchaseOrderItem, String> {
    fun deleteAllByPurchaseOrderId(id: String)
}