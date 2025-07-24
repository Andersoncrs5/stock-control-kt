package com.br.stock.control.util.filters.warehouse

import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.WareHouseEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class WareHouseCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : WareHouseCustomRepository {
    override fun findWithFilters(
        name: String?,
        description: String?,
        minAmount: Long?,
        maxAmount: Long?,
        minCubicMeters: Double?,
        maxCubicMeters: Double?,
        type: WareHouseEnum?,
        isActive: Boolean?,
        canToAdd: Boolean?,
        createdAtBefore: LocalDateTime?,
        createdAtAfter: LocalDateTime?,
        pageable: Pageable
    ): Page<Warehouse> {
        val criteria = mutableListOf<Criteria>()

        name?.let {
            criteria.add(Criteria.where("name").regex(".*$it.*", "i"))
        }

        description?.let {
            criteria.add(Criteria.where("description").regex(".*$it.*", "i"))
        }

        minAmount?.let {
            criteria.add(Criteria.where("amount").gte(it))
        }

        maxAmount?.let {
            criteria.add(Criteria.where("amount").lte(it))
        }

        minCubicMeters?.let {
            criteria.add(Criteria.where("capacityCubicMeters").gte(it))
        }

        maxCubicMeters?.let {
            criteria.add(Criteria.where("capacityCubicMeters").lte(it))
        }

        type?.let {
            criteria.add(Criteria.where("type").`is`(it))
        }

        isActive?.let {
            criteria.add(Criteria.where("isActive").`is`(it))
        }

        canToAdd?.let {
            criteria.add(Criteria.where("canToAdd").`is`(it))
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

        val wares = mongoTemplate.find(query, Warehouse::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Warehouse::class.java)

        return PageImpl(wares, pageable, total)
    }
}
