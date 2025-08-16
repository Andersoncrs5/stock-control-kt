package com.br.stock.control.service

import com.br.stock.control.model.dto.user.UpdateUserDTO
import com.br.stock.control.model.entity.User
import com.br.stock.control.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class UserService(
    private val repository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): User? {
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

    @Transactional(readOnly = true)
    fun existsByEmail(email: String): Boolean {
        return this.repository.existsByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getUserByName(name: String): User? {
        return this.repository.findByName(name)
    }

    @Transactional(readOnly = true)
    fun existsByName(name: String): Boolean {
        return this.repository.existsByName(name)
    }

    @Transactional(readOnly = true)
    fun getUserByRefreshToken(refreshToken: String): Optional<User>{
        logger.debug("Searching user by refreshtoken")
        val user = this.repository.findByRefreshToken(refreshToken)
        logger.debug("Returning user")
        return user
    }

    fun mergeUsersData(user: User, dto: UpdateUserDTO): User {
        if (dto.name.isNotBlank()) { user.name = dto.name.toString() }
        if (dto.fullName.isNotBlank()) { user.fullName = dto.fullName.toString() }
        return user
    }

    @Transactional
    fun changeStatusAccountNonExpired(user: User): User {
        logger.debug("Changing status AccountNonExpired")
        user.accountNonExpired = !user.accountNonExpired
        val save = this.repository.save(user)
        logger.debug("Status accountNonExpired changed")
        return save
    }

    @Transactional
    fun changeStatusAccountNonLocked(user: User): User {
        logger.debug("Changing status accountNonLocked")
        user.accountNonLocked = !user.accountNonLocked
        val save = this.repository.save(user)
        logger.debug("Status accountNonLocked changed")
        return save
    }

    @Transactional
    fun changeStatusCredentialsNonExpired(user: User): User {
        logger.debug("Changing status credentialsNonExpired")
        user.credentialsNonExpired = !user.credentialsNonExpired
        val save = this.repository.save(user)
        logger.debug("Status credentialsNonExpired changed")
        return save
    }

}