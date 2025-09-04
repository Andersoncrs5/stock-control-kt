package com.br.stock.control.repository

import com.br.stock.control.model.entity.PurchaseOrderItem
import com.br.stock.control.util.filters.purchaseOrderItem.PurchaseOrderItemCustomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface PurchaseOrderItemRepository: MongoRepository<PurchaseOrderItem, String>, PurchaseOrderItemCustomRepository {
    fun deleteAllByPurchaseOrderId(id: String)
    fun findAllByPurchaseOrderId(purchaseOrderId: String, pageable: Pageable): Page<PurchaseOrderItem>

}