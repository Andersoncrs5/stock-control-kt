package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.SupplierStatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "suppliers")
data class Supplier(
    @Id
    var userId: String? = null,
    var cnpj: String? = null,
    var nameEnterprise: String? = null,
    var notes: String? = null,
    var status: SupplierStatusEnum = SupplierStatusEnum.ACTIVE,
    var type: SupplierTypeEnum? = null,
    var rating: Int? = null,
    var categoriesId: MutableList<String> = mutableListOf(),
    var createdBy: String? = null,
    var isPreferred: Boolean = false,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate? = null,
    @LastModifiedDate
    var updatedAt: LocalDate? = null
)
