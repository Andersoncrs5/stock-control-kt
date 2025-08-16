package com.br.stock.control.util.filters.products

import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
class ProductCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): ProductCustomRepository {
    override fun findWithFilters(
        name: String?,
        sku: String?,
        barcode: String?,
        categoryId: String?,
        unitOfMeasure: UnitOfMeasureEnum?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        minCost: BigDecimal?,
        maxCost: BigDecimal?,
        isActive: Boolean?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<Product> {
        val criteria = mutableListOf<Criteria>()

        unitOfMeasure?.let {
            criteria.add(Criteria.where("unitOfMeasure").`is`(it))
        }

        isActive?.let {
            criteria.add(Criteria.where("isActive").`is`(it))
        }

        name?.let {
            criteria.add(Criteria.where("name").regex(".*$it.*", "i"))
        }

        sku?.let {
            criteria.add(Criteria.where("sku").regex(".*$it.*", "i"))
        }

        categoryId?.let {
            criteria.add(Criteria.where("categoryId").regex(".*$it.*", "i"))
        }

        barcode?.let {
            criteria.add(Criteria.where("barcode").regex(".*$it.*", "i"))
        }

        minPrice?.let {
            criteria.add(Criteria.where("price").gte(it))
        }

        maxPrice?.let {
            criteria.add(Criteria.where("price").lte(it))
        }

        minCost?.let {
            criteria.add(Criteria.where("cost").gte(it))
        }

        maxCost?.let {
            criteria.add(Criteria.where("cost").lte(it))
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

        val products = mongoTemplate.find(query, Product::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product::class.java)

        return PageImpl(products, pageable, total)
    }

}