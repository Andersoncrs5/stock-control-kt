package com.br.stock.control.service

import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.StatusEnum
import com.br.stock.control.repository.PurchaseOrderRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

@Service
class PurchaseOrderService(
    private val repository: PurchaseOrderRepository
) {

    private val logger = LoggerFactory.getLogger(PurchaseOrderService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<PurchaseOrder> {
        logger.debug("Getting PurchaseOrder by id: $id")
        val opt = this.repository.findById(id)
        logger.debug("Returning PurchaseOrder")
        return opt
    }

    @Transactional
    fun delete(order: PurchaseOrder) {
        this.logger.debug("Deleting order")
        this.repository.delete(order)
        this.logger.debug("Order deleted")
    }

    @Transactional
    fun deleteMany(id: List<String>) {
        this.logger.debug("Deleting many order")
        this.repository.deleteAllById(id)
        this.logger.debug("Order many deleted")
    }

    @Transactional
    fun create(order: PurchaseOrder): PurchaseOrder {
        logger.debug("Creating new PurchaseOrder")
        val purchaseOrder = this.repository.save(order)
        logger.debug("PurchaseOrder created")
        return purchaseOrder
    }

    @Transactional
    fun changeStatus(order: PurchaseOrder, status: StatusEnum): PurchaseOrder {
        logger.debug("Changing status order")
        order.status = status
        val order = this.repository.save(order)
        logger.debug("Status order changed")
        return order
    }

    @Transactional
    fun approvedOrder(order: PurchaseOrder, approvedBy: String): PurchaseOrder {
        this.logger.debug("Changing status order to APPROVE")

        if (order.status == StatusEnum.CANCELLED) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot change the status from cancelled to approved")
        }

        order.status = StatusEnum.APPROVED
        order.approvedByUserId = approvedBy
        order.approveAt = LocalDate.now()

        this.logger.debug("Status changed")
        val purchaseOrder = this.repository.save(order)

        this.logger.debug("Returning order with status changed")
        return purchaseOrder
    }

    @Transactional
    fun cancelOrder(order: PurchaseOrder, cancelBy: String, reasonCancel: String?): PurchaseOrder {
        this.logger.debug("Changing status order to CANCEL")

        if (order.status == StatusEnum.RECEIVED) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot change the status from received to cancel")
        }

        order.status = StatusEnum.CANCELLED
        order.canceledByUserId = cancelBy
        order.canceledAt = LocalDate.now()
        order.reasonCancel = reasonCancel

        this.logger.debug("Status changed to cancel")
        val purchaseOrder = this.repository.save(order)

        this.logger.debug("Returning order with status changed to cancel")
        return purchaseOrder
    }

    @Transactional
    fun receiveOrder(order: PurchaseOrder, receivedAt: LocalDate): PurchaseOrder {
        this.logger.debug("Changing status order to RECEIVED")

        if (order.status == StatusEnum.CANCELLED) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot change the status from cancel to RECEIVED")
        }

        order.status = StatusEnum.RECEIVED
        order.deliveryDate = receivedAt

        this.logger.debug("Status changed to RECEIVED")
        val purchaseOrder = this.repository.save(order)

        this.logger.debug("Returning order with status changed to RECEIVED")
        return purchaseOrder
    }

    @Transactional(readOnly = true)
    fun findAll(
        supplierId: String?, expectedDeliveryDateBefore: LocalDate?, expectedDeliveryDateAfter: LocalDate?,
        currency: CurrencyEnum?, deliveryDateBefore: LocalDate?, deliveryDateAfter: LocalDate?, status: StatusEnum?,
        receivedAtBefore: LocalDate?, receivedAtAfter: LocalDate?, totalAmountMin: BigDecimal?, totalAmountMax: BigDecimal?,
        shippingCostMin: BigDecimal?, shippingCostMax: BigDecimal?, placedByUserId: String?, approvedByUserId: String?,
        approveAtBefore: LocalDate?, approveAtAfter: LocalDate?, canceledByUserId: String?, canceledAtBefore: LocalDate?,
        canceledAtAfter: LocalDate?, reasonCancel: String?, notes: String?, createdAtBefore: LocalDate?, createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<PurchaseOrder> {
        return this.repository.findAll(
            supplierId, expectedDeliveryDateBefore, expectedDeliveryDateAfter, currency,
            deliveryDateBefore, deliveryDateAfter, status, receivedAtBefore, receivedAtAfter,
            totalAmountMin, totalAmountMax, shippingCostMin, shippingCostMax, placedByUserId,
            approvedByUserId, approveAtBefore, approveAtAfter, canceledByUserId, canceledAtBefore,
            canceledAtAfter, reasonCancel,notes, createdAtBefore, createdAtAfter, pageable
        )

    }

}