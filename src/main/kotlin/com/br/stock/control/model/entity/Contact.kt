package com.br.stock.control.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDate

data class Contact(
    @Id
    var userId: String? = null,
    var secondaryEmail: String? = null,
    var phone: String? = null,
    var secondaryPhone: String? = null,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate = LocalDate.now(),
    @LastModifiedDate
    var updatedAt: LocalDate? = null
)