package com.br.stock.control.service

import com.br.stock.control.model.entity.User
import com.br.stock.control.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class UserService(
    private val repository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getUser(id: String): User {
        if (id.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        val user: Optional<User> = this.repository.findById(id);

        if (user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return user.get();
    }

    @Transactional
    fun deleteUser(user: User) {
        if (user.id.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");

        this.repository.delete(user);
    }



}