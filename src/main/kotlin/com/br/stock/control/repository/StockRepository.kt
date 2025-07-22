package com.br.stock.control.repository

import com.br.stock.control.model.entity.Stock
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface StockRepository: MongoRepository<Stock, String> {
}