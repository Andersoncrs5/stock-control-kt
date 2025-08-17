package com.br.stock.control.service

import com.br.stock.control.model.dto.stock.UpdateStockDTO
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.repository.StockRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        productId: String?,
        minQuantity: Int?,
        maxQuantity: Int?,
        responsibleUserId: String?,
        warehouseId: String?,
        isActive: Boolean?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<Stock> {
        val stocks: Page<Stock> = this.repository.findAll(productId,minQuantity,maxQuantity,responsibleUserId,warehouseId,isActive, createdAtBefore, createdAtAfter, pageable)
        return stocks
    }

}