package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.TypeAddressEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "address")
data class Address(
    @Id
    var id: String,
    var street: String,
    var number: String? = null,
    var complement: String? = null,
    var neighborhood: String,
    var city: String,
    var state: String,
    var zipCode: String,
    var country: String,
    var referencePoint: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var isActive: Boolean = true,
    var type: TypeAddressEnum,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)