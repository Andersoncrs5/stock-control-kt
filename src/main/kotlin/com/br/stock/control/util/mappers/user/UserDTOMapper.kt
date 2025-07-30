package com.br.stock.control.util.mappers.user

import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.User
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class UserDTOMapper(
    private val mapper: Mapper
) {
    fun toDTO(user: User): UserDTO {
        return this.mapper.map(user, UserDTO::class.java)
    }

    fun toUser(dto: UserDTO): User {
        return this.mapper.map(dto, User::class.java)
    }
}