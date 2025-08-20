package com.br.stock.control.service

import com.br.stock.control.model.dto.stock.UpdateStockDTO
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.model.enum.MovementTypeEnum
import com.br.stock.control.repository.StockRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.Optional

@Service
class StockService(
    private val repository: StockRepository
) {
    private val logger = LoggerFactory.getLogger(StockService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<Stock> {
        logger.debug("Getting stock by id")
        val stock: Optional<Stock> = this.repository.findById(id)
        logger.debug("Returning stock")
        return stock
    }

    @Transactional
    fun delete(stock: Stock) {
        logger.debug("Deleting stock")
        this.repository.delete(stock)
        logger.debug("Stock deleted")
    }

    @Transactional
    fun deleteByProductId(productId: String) {
        logger.debug("Deleting stock by productId")
        this.repository.deleteAllByProductId(productId)
        logger.debug("Stocks deleted")
    }

    @Transactional
    fun deleteByWarehouseId(warehouseId: String) {
        logger.debug("Deleting stock by warehouseId")
        this.repository.deleteAllByWarehouseId(warehouseId)
        logger.debug("Stocks deleted for warehouseId")
    }

    @Transactional
    fun create(stock: Stock): Stock {
        logger.debug("Creating a stock")
        val save = this.repository.save<Stock>(stock)
        logger.debug("Stock created")
        return save
    }

    @Transactional
    fun update(stock: Stock, dto: UpdateStockDTO): Stock {
        logger.debug("Updating stock")
        stock.quantity = dto.quantity
        stock.responsibleUserId = dto.responsibleUserId
        stock.warehouseId = dto.warehouseId

        val save = this.repository.save(stock)
        logger.debug("Stock updated")
        return save
    }

    @Transactional
    fun changeStatus(stock: Stock): Stock {
        logger.debug("Changing status active")
        stock.isActive = !stock.isActive
        return this.repository.save(stock)
    }

    @Transactional(readOnly = true)
    fun findAll(
        productId: String?, minQuantity: Int?, maxQuantity: Int?,
        responsibleUserId: String?, warehouseId: String?, isActive: Boolean?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable
    ): Page<Stock> {
        val stocks: Page<Stock> = this.repository.findAll(productId,minQuantity,maxQuantity,responsibleUserId,warehouseId,isActive, createdAtBefore, createdAtAfter, pageable)
        return stocks
    }

    @Transactional(readOnly = true)
    fun existsById(id: String): Boolean {
        return this.repository.existsById(id)
    }

    @Transactional
    fun moveStock(
        stockOrigin: Stock, stockDestination: Stock, quantity: Long
    ): Map<Int, Stock> {
        if (quantity > stockOrigin.quantity) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Insufficient stock: available ${stockOrigin.quantity}"
            )
        }

        stockOrigin.quantity -= quantity
        stockDestination.quantity += quantity

        val updatedOrigin = this.repository.save(stockOrigin)
        val updatedDestination = this.repository.save(stockDestination)

        return mapOf(
            0 to updatedOrigin,
            1 to updatedDestination
        )
    }

    fun adjustQuantity(stock: Stock, move: StockMovement): Stock {
        when (move.movementType) {
            MovementTypeEnum.IN -> stock.quantity += move.quantity

            MovementTypeEnum.OUT -> {
                validateQuantity(stock, move)
                stock.quantity -= move.quantity
            }

            MovementTypeEnum.DISCARD -> {
                validateQuantity(stock, move)
                stock.quantity -= move.quantity
            }

            MovementTypeEnum.ADJUSTMENT -> stock.quantity = move.quantity

            MovementTypeEnum.TRANSFER -> {
                throw UnsupportedOperationException("Transfer operation not yet implemented")
            }

            null -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Movement type cannot be null")
        }

        return repository.save(stock)
    }

    private fun validateQuantity(stock: Stock, move: StockMovement) {
        if (stock.quantity < move.quantity) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Insufficient stock: available ${stock.quantity}, required ${move.quantity}"
            )
        }
    }

}