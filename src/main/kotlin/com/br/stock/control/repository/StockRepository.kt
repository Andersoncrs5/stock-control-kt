package com.br.stock.control.repository

import com.br.stock.control.model.entity.Stock
import com.br.stock.control.util.filters.stock.StockCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface StockRepository: MongoRepository<Stock, String>, StockCustomRepository {
    fun deleteAllByProductId(productId: String)
    fun deleteAllByWarehouseId(warehouseId: String)
}