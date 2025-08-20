package com.br.stock.control.util.filters.stockMovement

import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.model.enum.MovementTypeEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate

class StockMovementCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): StockMovementCustomRepository {
    override fun findAll(
        stockId: String?,
        productId: String?,
        movementType: MovementTypeEnum?,
        minQuantity: Long?,
        maxQuantity: Long?,
        reason: String?,
        responsibleUserId: String?,
        notes: String?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<StockMovement> {
        val criteria = mutableListOf<Criteria>()

        stockId?.let {
            criteria.add(Criteria.where("stockId").regex(".*$it.*", "i"))
        }

        productId?.let {
            criteria.add(Criteria.where("productId").regex(".*$it.*", "i"))
        }

        movementType?.let {
            criteria.add(Criteria.where("movementType").`is`(it))
        }

        reason?.let {
            criteria.add(Criteria.where("reason").regex(".*$it.*", "i"))
        }

        responsibleUserId?.let {
            criteria.add(Criteria.where("responsibleUserId").regex(".*$it.*", "i"))
        }

        notes?.let {
            criteria.add(Criteria.where("notes").regex(".*$it.*", "i"))
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

        val categories = mongoTemplate.find(query, StockMovement::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), StockMovement::class.java)

        return PageImpl(categories, pageable, total)
    }

}