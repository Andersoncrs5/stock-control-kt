package com.br.stock.control.util.filters.supplier

import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.enum.SupplierStatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate

class SupplierCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): SupplierCustomRepository {
    override fun findAll(
        userId: String?, cnpj: String?, nameEnterprise: String?, notes: String?,
        status: SupplierStatusEnum?, type: SupplierTypeEnum?,
        minRating: Int?, maxRating: Int?,
        categoriesId: List<String>?,
        createdBy: String?, isPreferred: Boolean?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable
    ): Page<Supplier> {
        val criteria = mutableListOf<Criteria>()

        userId?.let {
            criteria.add(Criteria.where("userId").regex(".*$it.*", "i"))
        }

        cnpj?.let {
            criteria.add(Criteria.where("cnpj").regex(".*$it.*", "i"))
        }

        nameEnterprise?.let {
            criteria.add(Criteria.where("nameEnterprise").regex(".*$it.*", "i"))
        }

        notes?.let {
            criteria.add(Criteria.where("notes").regex(".*$it.*", "i"))
        }

        status?.let {
            criteria.add(Criteria.where("status").`is`(it))
        }

        type?.let {
            criteria.add(Criteria.where("type").`is`(it))
        }

        categoriesId?.let {
            if (it.isNotEmpty()) {
                criteria.add(Criteria.where("categoriesId").`in`(it))
            }
        }

        minRating?.let {
            criteria.add(Criteria.where("rating").gte(it))
        }

        maxRating?.let {
            criteria.add(Criteria.where("rating").lte(it))
        }

        isPreferred?.let {
            criteria.add(Criteria.where("isPreferred").`is`(it))
        }

        createdBy?.let {
            criteria.add(Criteria.where("createdBy").regex(".*$it.*", "i"))
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

        val suppliers = mongoTemplate.find(query, Supplier::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Supplier::class.java)

        return PageImpl(suppliers, pageable, total)
    }
}