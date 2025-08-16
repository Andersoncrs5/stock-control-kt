package com.br.stock.control.model.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "roles")
data class Role(
    @Id
    var id: String,
    var name: String,
    var description: String?,
    @CreatedDate
    var createdAt: LocalDate,
    @LastModifiedDate
    var updatedAt: LocalDate
)