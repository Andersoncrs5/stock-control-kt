package com.br.stock.control.model.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    var id: ObjectId,
    @Indexed(unique = true)
    var username: String,
    @Indexed(unique = true)
    var email: String,
    var password: String,
    var fullName: String,
    var isEnabled: Boolean = true,
    var accountNonExpired: Boolean = true,
    var credentialsNonExpired: Boolean = true,
    var accountNonLocked: Boolean = true,
    var roles: Set<Role> = emptySet(),
    var lastLoginAt: LocalDateTime? = null,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
