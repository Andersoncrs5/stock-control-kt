package com.br.stock.control.util.filters.purchaseOrder

import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.StatusEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.math.BigDecimal
import java.time.LocalDate

class PurchaseOrderCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): PurchaseOrderCustomRepository {
    override fun findAll(
        supplierId: String?, expectedDeliveryDateBefore: LocalDate?, expectedDeliveryDateAfter: LocalDate?,
        currency: CurrencyEnum?, deliveryDateBefore: LocalDate?, deliveryDateAfter: LocalDate?, status: StatusEnum?,
        receivedAtBefore: LocalDate?, receivedAtAfter: LocalDate?, totalAmountMin: BigDecimal?, totalAmountMax: BigDecimal?,
        shippingCostMin: BigDecimal?, shippingCostMax: BigDecimal?, placedByUserId: String?, approvedByUserId: String?,
        approveAtBefore: LocalDate?, approveAtAfter: LocalDate?, canceledByUserId: String?,canceledAtBefore: LocalDate?,
        canceledAtAfter: LocalDate?, reasonCancel: String?, notes: String?, createdAtBefore: LocalDate?, createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<PurchaseOrder> {
        val criteria = mutableListOf<Criteria>()

        supplierId?.let {
            criteria.add(Criteria.where("supplierId").regex(".*$it.*", "i"))
        }

        expectedDeliveryDateBefore?.let {
            criteria.add(Criteria.where("expectedDeliveryDate").lte(it))
        }

        expectedDeliveryDateAfter?.let {
            criteria.add(Criteria.where("expectedDeliveryDate").gte(it))
        }

        currency?.let {
            criteria.add(Criteria.where("currency").`is`(it))
        }

        deliveryDateBefore?.let {
            criteria.add(Criteria.where("deliveryDate").lte(it))
        }

        deliveryDateAfter?.let {
            criteria.add(Criteria.where("deliveryDate").gte(it))
        }

        status?.let {
            criteria.add(Criteria.where("status").`is`(it))
        }

        receivedAtBefore?.let {
            criteria.add(Criteria.where("receivedAt").lte(it))
        }

        receivedAtAfter?.let {
            criteria.add(Criteria.where("receivedAt").gte(it))
        }

        totalAmountMin?.let {
            criteria.add(Criteria.where("totalAmount").gte(it))
        }

        totalAmountMax?.let {
            criteria.add(Criteria.where("totalAmount").lte(it))
        }

        shippingCostMin?.let {
            criteria.add(Criteria.where("shippingCost").gte(it))
        }

        shippingCostMax?.let {
            criteria.add(Criteria.where("shippingCost").lte(it))
        }

        placedByUserId?.let {
            criteria.add(Criteria.where("placedByUserId").regex(".*$it.*", "i"))
        }

        approvedByUserId?.let {
            criteria.add(Criteria.where("approvedByUserId").regex(".*$it.*", "i"))
        }

        approveAtBefore?.let {
            criteria.add(Criteria.where("approveAt").lte(it))
        }

        approveAtAfter?.let {
            criteria.add(Criteria.where("approveAt").gte(it))
        }

        canceledByUserId?.let {
            criteria.add(Criteria.where("canceledByUserId").regex(".*$it.*", "i"))
        }

        canceledAtBefore?.let {
            criteria.add(Criteria.where("canceledAt").lte(it))
        }

        canceledAtAfter?.let {
            criteria.add(Criteria.where("canceledAt").gte(it))
        }

        reasonCancel?.let {
            criteria.add(Criteria.where("reasonCancel").regex(".*$it.*", "i"))
        }

        notes?.let {
            criteria.add(Criteria.where("notes").regex(".*$it.*", "i"))
        }

        createdAtBefore?.let {
            criteria.add(Criteria.where("createdAt").lte(it))
        }

        createdAtAfter?.let {
            criteria.add(Criteria.where("createdAt").gte(it))
        }

        val query = Query().apply {
            if (criteria.isNotEmpty()) {
                addCriteria(Criteria().andOperator(*criteria.toTypedArray()))
            }
            with(pageable)
        }

        val products = mongoTemplate.find(query, PurchaseOrder::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), PurchaseOrder::class.java)

        return PageImpl(products, pageable, total)
    }

}