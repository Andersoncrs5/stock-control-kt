package com.br.stock.control.util.mappers.address

import com.br.stock.control.model.dto.address.UpdateAddressDTO
import com.br.stock.control.model.entity.Address
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class UpdateAddressMapper(
    private val mapper: Mapper
) {
    fun toDTO(address: Address): UpdateAddressDTO {
        return this.mapper.map(address, UpdateAddressDTO::class.java)
    }

    fun toAddress(dto: UpdateAddressDTO): Address {
        return this.mapper.map(dto, Address::class.java)
    }
}