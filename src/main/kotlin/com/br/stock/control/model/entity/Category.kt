package com.br.stock.control.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDate

data class Category(
    @Id
    var id: String = "",
    var name: String = "",
    var description: String? = "",
    var active: Boolean = true,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate = LocalDate.now(),
    @LastModifiedDate
    var updatedAt: LocalDate = LocalDate.now()
)
