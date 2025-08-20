package com.br.stock.control.service

import com.br.stock.control.model.entity.Stock
import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.model.enum.MovementTypeEnum
import com.br.stock.control.repository.StockMovementRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
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
    fun deleteManyByStockId(id: String) {
        logger.debug("Deleting many stock movement by stockId ")
        this.repository.deleteAllByStockId(id)
        logger.debug("Stock movements many deleted by stockId")
    }

    @Transactional
    fun create(move: StockMovement): StockMovement {
        logger.debug("Creating a StockMovement")
        val movement = this.repository.save(move)
        logger.debug("StockMovement created")
        return movement
    }

    @Transactional(readOnly = true)
    fun findAll(
        stockId: String?, productId: String?, movementType: MovementTypeEnum?,
        minQuantity: Long?, maxQuantity: Long?, reason: String?,
        responsibleUserId: String?, notes: String?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable
    ): Page<StockMovement> {
        return this.repository.findAll(
            stockId, productId, movementType, minQuantity,
            maxQuantity, reason, responsibleUserId, notes,
            createdAtBefore, createdAtAfter, pageable
        )
    }

}