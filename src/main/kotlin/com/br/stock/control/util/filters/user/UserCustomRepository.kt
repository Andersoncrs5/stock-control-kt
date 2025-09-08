package com.br.stock.control.util.filters.user

import com.br.stock.control.model.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface UserCustomRepository {
    fun findAll(
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
    ): Page<User>
}