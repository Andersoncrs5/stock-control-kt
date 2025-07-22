package com.br.stock.control.repository

import com.br.stock.control.model.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.userdetails.UserDetails

interface UserRepository: MongoRepository<User, String> {
    fun findByEmail(email: String): UserDetails?
}