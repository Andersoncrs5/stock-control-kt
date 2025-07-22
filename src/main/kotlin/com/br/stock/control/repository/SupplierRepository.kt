package com.br.stock.control.repository

import com.br.stock.control.model.entity.Supplier
import org.springframework.data.mongodb.repository.MongoRepository

interface SupplierRepository: MongoRepository<Supplier, String> {
}