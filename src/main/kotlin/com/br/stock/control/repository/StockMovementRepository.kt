package com.br.stock.control.repository

import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.util.filters.stockMovement.StockMovementCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface StockMovementRepository: MongoRepository<StockMovement, String>, StockMovementCustomRepository {
}