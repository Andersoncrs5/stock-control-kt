package com.br.stock.control.util.filters.stock

import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Stock
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate

class StockCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): StockCustomRepository {
    override fun findAll(
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
        val criteria = mutableListOf<Criteria>()

        productId?.let {
            criteria.add(Criteria.where("productId").regex(".*$it.*", "i"))
        }

        minQuantity?.let {
            criteria.add(Criteria.where("quantity").gte(it))
        }

        maxQuantity?.let {
            criteria.add(Criteria.where("quantity").lte(it))
        }

        responsibleUserId?.let {
            criteria.add(Criteria.where("responsibleUserId").regex(".*$it.*", "i"))
        }

        warehouseId?.let {
            criteria.add(Criteria.where("warehouseId").regex(".*$it.*", "i"))
        }

        isActive?.let {
            criteria.add(Criteria.where("isActive").`is`(it))
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

        val categories = mongoTemplate.find(query, Stock::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Category::class.java)

        return PageImpl(categories, pageable, total)
    }
}