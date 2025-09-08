package com.br.stock.control.repository

import com.br.stock.control.model.entity.Role
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface RoleRepository: MongoRepository<Role, String> {
    fun existsByName(name: String): Boolean
    fun findByName(name: String): Optional<Role>

}