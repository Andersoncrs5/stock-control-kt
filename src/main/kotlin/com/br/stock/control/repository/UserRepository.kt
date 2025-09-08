package com.br.stock.control.repository

import com.br.stock.control.model.entity.User
import com.br.stock.control.util.filters.user.UserCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.userdetails.UserDetails
import java.util.Optional

interface UserRepository: MongoRepository<User, String>, UserCustomRepository {
    fun findByEmail(email: String): UserDetails?
    fun existsByEmail(email: String): Boolean
    fun existsByName(email: String): Boolean
    fun findByName(name: String): User?
    fun findByRefreshToken(refreshToken: String): Optional<User>
}