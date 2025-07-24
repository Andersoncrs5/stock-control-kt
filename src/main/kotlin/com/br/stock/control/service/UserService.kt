package com.br.stock.control.service

import com.br.stock.control.model.entity.User
import com.br.stock.control.repository.UserRepository
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Optional
import java.util.UUID

@Slf4j
@Service
class UserService(
    private val repository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(readOnly = true)
    fun getUser(id: String): User? {
        logger.debug("Getting user in database...")
        val orElse: User? = this.repository.findById(id).orElse(null)
        logger.debug("Get user! returning")
        return orElse
    }

    @Transactional
    fun deleteUser(user: User) {
        logger.debug("Deleting user....")
        this.repository.delete(user)
        logger.debug("User deleted!")
    }

    @Transactional
    fun saveUser(user: User): User {
        logger.debug("Saving user in database...")
        val save = repository.save(user)
        logger.debug("User saved in database")
        return save
    }

    @Transactional
    fun updateUser(user: User): User {
        logger.debug("Updating user.....")
        val save = this.repository.save<User>(user)
        logger.debug("User updated!")
        return save
    }

    @Transactional
    fun deleteMany(ids: List<String>) {
        logger.debug("Deleting many user by id")
        this.repository.deleteAllById(ids)
        logger.debug("Users deleted!")
    }

}