package com.br.stock.control.service

import com.br.stock.control.model.entity.Role
import com.br.stock.control.model.entity.User
import com.br.stock.control.repository.RoleRepository
import com.br.stock.control.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class RoleService(
    private val repository: RoleRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(RoleService::class.java)

    @Transactional(readOnly = true)
    fun getById(id: String): Optional<Role> {
        logger.debug("Getting role by id: $id")
        val opt = this.repository.findById(id)
        return opt
    }

    @Transactional(readOnly = true)
    fun getByName(name: String): Optional<Role> {
        logger.debug("Getting role by name: $name")
        val opt = this.repository.findByName(name)
        return opt
    }

    @Transactional
    fun setRoleToUser(user: User, role: Role): User {
        if (user.roles.any { it.name == role.name }) {
            return user
        }

        val updatedRoles = user.roles.toMutableSet().apply { add(role) }
        user.roles = updatedRoles

        return userRepository.save(user)
    }

    @Transactional
    fun removeRoleFromUser(user: User, role: Role): User {
        if (user.roles.none { it.name == role.name }) {
            return user
        }

        val updatedRoles = user.roles.toMutableSet().apply { removeIf { it.name == role.name } }
        user.roles = updatedRoles

        return userRepository.save(user)
    }

}