package com.br.stock.control.model.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

data class Address(
    @Id
    var id: ObjectId,
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
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)