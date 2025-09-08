package com.br.stock.control.util.filters.user

import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate

class UserCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate
): UserCustomRepository {
    override fun findAll(
        name: String?,
        email: String?,
        fullName: String?,
        accountNonExpired: Boolean?,
        credentialsNonExpired: Boolean?,
        accountNonLocked: Boolean?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable,
        roleName: String?,
    ): Page<User> {
        val criteria = mutableListOf<Criteria>()

        roleName?.let {
            criteria.add(Criteria.where("roles.name").regex(".*$it.*", "i"))
        }

        name?.let {
            criteria.add(Criteria.where("name").regex(".*$it.*", "i"))
        }

        email?.let {
            criteria.add(Criteria.where("email").regex(".*$it.*", "i"))
        }

        fullName?.let {
            criteria.add(Criteria.where("fullName").regex(".*$it.*", "i"))
        }

        accountNonExpired?.let {
            criteria.add(Criteria.where("accountNonExpired").`is`(it))
        }

        credentialsNonExpired?.let {
            criteria.add(Criteria.where("credentialsNonExpired").`is`(it))
        }

        accountNonLocked?.let {
            criteria.add(Criteria.where("accountNonLocked").`is`(it))
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

        val suppliers = mongoTemplate.find(query, User::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), User::class.java)

        return PageImpl(suppliers, pageable, total)
    }
}