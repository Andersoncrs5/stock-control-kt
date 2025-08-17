package com.br.stock.control.repository

import com.br.stock.control.model.entity.Role
import org.springframework.data.mongodb.repository.MongoRepository

interface RoleRepository: MongoRepository<Role, String> {
}