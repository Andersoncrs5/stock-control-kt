package com.br.stock.control.util.facades

import com.br.stock.control.util.mappers.user.UserDTOMapper
import org.springframework.stereotype.Service

@Service
class FacadeMappers(
    val userDTOMapper: UserDTOMapper
) {
}