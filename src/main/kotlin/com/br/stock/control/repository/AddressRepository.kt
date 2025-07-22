package com.br.stock.control.repository

import com.br.stock.control.model.entity.Address
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface AddressRepository: MongoRepository<Address, String> {
}