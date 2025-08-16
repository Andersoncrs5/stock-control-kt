package com.br.stock.control.util.filters.category

import com.br.stock.control.model.entity.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CategoryCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): CategoryCustomRepository {
    override fun filter(
        name: String?,
        description: String?,
        active: Boolean?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<Category> {
        val criteria = mutableListOf<Criteria>()

        name?.let {
            criteria.add(Criteria.where("name").regex(".*$it.*", "i"))
        }

        description?.let {
            criteria.add(Criteria.where("description").regex(".*$it.*", "i"))
        }

        active?.let {
            criteria.add(Criteria.where("active").`is`(it))
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

        val categories = mongoTemplate.find(query, Category::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Category::class.java)

        return PageImpl(categories, pageable, total)
    }
}