package com.br.stock.control.util.mappers.address

import com.br.stock.control.model.dto.address.CreateAddressDTO
import com.br.stock.control.model.entity.Address
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateAddressMapper(
    private val mapper: Mapper
) {
    fun toDTO(address: Address): CreateAddressDTO {
        return this.mapper.map(address, CreateAddressDTO::class.java)
    }

    fun toAddress(dto: CreateAddressDTO): Address {
        return this.mapper.map(dto, Address::class.java)
    }

}