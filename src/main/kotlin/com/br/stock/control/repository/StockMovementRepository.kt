package com.br.stock.control.repository

import com.br.stock.control.model.entity.StockMovement
import org.springframework.data.mongodb.repository.MongoRepository

interface StockMovementRepository: MongoRepository<StockMovement, String> {
}