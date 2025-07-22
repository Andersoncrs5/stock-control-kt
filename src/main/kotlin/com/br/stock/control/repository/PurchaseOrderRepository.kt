package com.br.stock.control.repository

import com.br.stock.control.model.entity.PurchaseOrder
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PurchaseOrderRepository: MongoRepository<PurchaseOrder, ObjectId> {
}