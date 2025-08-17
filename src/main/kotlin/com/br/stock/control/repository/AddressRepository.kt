package com.br.stock.control.repository

import com.br.stock.control.model.entity.Address
import com.br.stock.control.model.enum.TypeAddressEnum
import org.springframework.data.mongodb.repository.MongoRepository

interface AddressRepository: MongoRepository<Address, String> {
    fun existsByIdAndType(id: String, type: TypeAddressEnum): Boolean
}