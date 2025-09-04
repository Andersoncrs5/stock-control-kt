package com.br.stock.control.util.filters.purchaseOrderItem

import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.entity.PurchaseOrderItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.math.BigDecimal
import java.time.LocalDate

class PurchaseOrderItemCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): PurchaseOrderItemCustomRepository {
    override fun findAll(
        purchaseOrderId: String?,
        productId: String?,
        minQuantity: Int?,
        maxQuantity: Int?,
        minExpectedQuantity: Int?,
        maxExpectedQuantity: Int?,
        minBackOrderedQuantity: Int?,
        maxBackOrderedQuantity: Int?,
        minReceivedQuantity: Int?,
        maxReceivedQuantity: Int?,
        minUnitPrice: BigDecimal?,
        maxUnitPrice: BigDecimal?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<PurchaseOrderItem> {
        val criteria = mutableListOf<Criteria>()

        purchaseOrderId?.let {
            criteria.add(Criteria.where("purchaseOrderId").regex(".*$it.*", "i"))
        }

        productId?.let {
            criteria.add(Criteria.where("productId").regex(".*$it.*", "i"))
        }

        minReceivedQuantity?.let {
            criteria.add(Criteria.where("receivedQuantity").gte(it))
        }

        maxReceivedQuantity?.let {
            criteria.add(Criteria.where("receivedQuantity").lte(it))
        }

        minBackOrderedQuantity?.let {
            criteria.add(Criteria.where("backOrderedQuantity").gte(it))
        }

        maxBackOrderedQuantity?.let {
            criteria.add(Criteria.where("backOrderedQuantity").lte(it))
        }

        minExpectedQuantity?.let {
            criteria.add(Criteria.where("expectedQuantity").gte(it))
        }

        maxExpectedQuantity?.let {
            criteria.add(Criteria.where("expectedQuantity").lte(it))
        }

        minQuantity?.let {
            criteria.add(Criteria.where("quantity").gte(it))
        }

        maxQuantity?.let {
            criteria.add(Criteria.where("quantity").lte(it))
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

        val products = mongoTemplate.find(query, PurchaseOrderItem::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), PurchaseOrderItem::class.java)

        return PageImpl(products, pageable, total)
    }
}