package com.br.stock.control.model.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "suppliers")
data class Supplier(
    @Id
    var id: ObjectId,
    var name: String,
    var contactPerson: String,
    var email: String,
    var phone: String,
    var address: String,
    var cnpj: String,
    var notes: String,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
