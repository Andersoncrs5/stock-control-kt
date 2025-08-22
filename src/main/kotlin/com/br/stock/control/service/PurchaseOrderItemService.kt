package com.br.stock.control.service

import com.br.stock.control.model.dto.purchaseOrderItem.UpdateOrderItemDTO
import com.br.stock.control.model.entity.PurchaseOrderItem
import com.br.stock.control.repository.PurchaseOrderItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Optional

@Service
class PurchaseOrderItemService(
    private val repository: PurchaseOrderItemRepository
) {
    private val logger = LoggerFactory.getLogger(PurchaseOrderItemService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<PurchaseOrderItem> {
        logger.debug("Getting purchaseOrderItem")
        val opt: Optional<PurchaseOrderItem> = this.repository.findById(id)
        logger.debug("Returning purchaseOrderItem")
        return opt
    }

    @Transactional
    fun delete(item: PurchaseOrderItem) {
        logger.debug("Deleting PurchaseOrderItem by id: ${item.id}")
        this.repository.delete(item)
        logger.debug("PurchaseOrderItem deleted with id: ${item.id}")
    }

    @Transactional
    fun deleteMany(id: List<String>) {
        logger.debug("Deleting many PurchaseOrderItem")
        this.repository.deleteAllById(id)
        logger.debug("Many PurchaseOrderItem deleted")
    }

    @Transactional
    fun deleteManyByPurchaseOrderId(id: String) {
        logger.debug("Deleting many PurchaseOrderItem by purchaseOrderId")
        this.repository.deleteAllByPurchaseOrderId(id)
        logger.debug("Many PurchaseOrderItem deleted by purchaseOrderId")
    }

    @Transactional
    fun create(item: PurchaseOrderItem): PurchaseOrderItem {
        this.logger.debug("Creating new purchaseOrderItem")
        item.createdAt = LocalDate.now()
        val orderItem = this.repository.save(item)
        this.logger.debug("PurchaseOrderItem created! id is : ${orderItem.id}")
        return orderItem
    }

    @Transactional
    fun update(item: PurchaseOrderItem, dto: UpdateOrderItemDTO): PurchaseOrderItem {
        logger.debug("Updating purchaseOrderItem by id: ${item.id}")
        item.productId = dto.productId
        item.quantity = dto.quantity
        item.unitPrice = dto.unitPrice
        item.expectedQuantity = dto.expectedQuantity
        item.backOrderedQuantity = dto.backOrderedQuantity
        item.receivedQuantity = dto.receivedQuantity

        val orderItem = this.repository.save(item)

        logger.debug("PurchaseOrderItem updated")

        return orderItem
    }

    @Transactional
    fun createMany(item: List<PurchaseOrderItem>): List<PurchaseOrderItem> {
        logger.debug("Creating multi PurchaseOrderItem")
        val items = this.repository.saveAll(item)
        logger.debug("Multi PurchaseOrderItem created")

        return items
    }

}