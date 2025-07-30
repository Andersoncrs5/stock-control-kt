package com.br.stock.control.util.mappers.user

import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.entity.User
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class RegisterDTOtoUserMapper(
    private val mapper: Mapper
) {
    fun toDTO(user: User): RegisterUserDTO {
        return mapper.map(user, RegisterUserDTO::class.java)
    }

    fun toUser(dto: RegisterUserDTO): User {
        return mapper.map(dto, User::class.java)
    }
}
