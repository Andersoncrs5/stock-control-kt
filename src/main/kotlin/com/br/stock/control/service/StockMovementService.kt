package com.br.stock.control.service

import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.repository.StockMovementRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class StockMovementService(
    private val repository: StockMovementRepository
) {
    private val logger = LoggerFactory.getLogger(StockMovementService::class.java)

    fun get(id: String): Optional<StockMovement> {
        logger.debug("getting stockMovement")
        val optional: Optional<StockMovement> = this.repository.findById(id)
        logger.debug("Returning stockMovement")
        return optional
    }



}