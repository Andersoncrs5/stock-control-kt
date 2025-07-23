package com.br.stock.control.util.filters.products

import com.br.stock.control.model.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class ProductCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): ProductCustomRepository {
    override fun findWithFilters(
        name: String?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        pageable: Pageable
    ): Page<Product> {
        val criteria = mutableListOf<Criteria>()

        name?.let {
            criteria.add(Criteria.where("name").regex(".*$it.*", "i"))
        }

        minPrice?.let {
            criteria.add(Criteria.where("price").gte(it))
        }

        maxPrice?.let {
            criteria.add(Criteria.where("price").lte(it))
        }

        val query = Query().apply {
            if (criteria.isNotEmpty()) {
                addCriteria(Criteria().andOperator(*criteria.toTypedArray()))
            }
            with(pageable)
        }

        val products = mongoTemplate.find(query, Product::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product::class.java)

        return PageImpl(products, pageable, total)
    }

}