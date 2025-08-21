package com.br.stock.control.repository

import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.util.filters.purchaseOrder.PurchaseOrderCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface PurchaseOrderRepository: MongoRepository<PurchaseOrder, String>, PurchaseOrderCustomRepository