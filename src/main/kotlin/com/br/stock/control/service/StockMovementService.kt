package com.br.stock.control.service

import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.repository.StockMovementRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class StockMovementService(
    private val repository: StockMovementRepository
) {
    private val logger = LoggerFactory.getLogger(StockMovementService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<StockMovement> {
        logger.debug("getting stockMovement")
        val optional: Optional<StockMovement> = this.repository.findById(id)
        logger.debug("Returning stockMovement")
        return optional
    }

    @Transactional
    fun delete(stock: StockMovement) {
        logger.debug("Deleting stock movement")
        this.repository.delete(stock)
        logger.debug("Stock movement deleted")
    }

    @Transactional
    fun deleteMany(ids: List<String>) {
        logger.debug("Deleting many stock movement")
        this.repository.deleteAllById(ids)
        logger.debug("Stock movements many deleted")
    }

    @Transactional
    fun create(move: StockMovement): StockMovement {
        logger.debug("Creating a StockMovement")
        val movement = this.repository.save(move)
        logger.debug("StockMovement created")
        return movement
    }


}