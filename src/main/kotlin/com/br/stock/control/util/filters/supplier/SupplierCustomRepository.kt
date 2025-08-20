package com.br.stock.control.util.filters.supplier

import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.enum.SupplierStatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SupplierCustomRepository {
    fun findAll(
        userId: String?,
        cnpj: String?,
        nameEnterprise: String?,
        notes: String?,
        status: SupplierStatusEnum?,
        type: SupplierTypeEnum?,
        minRating: Int?,
        maxRating: Int?,
        categoriesId: List<String>?,
        createdBy: String?,
        isPreferred: Boolean?,
        createdAtBefore: LocalDate?,
        createdAtAfter: LocalDate?,
        pageable: Pageable
    ): Page<Supplier>
}