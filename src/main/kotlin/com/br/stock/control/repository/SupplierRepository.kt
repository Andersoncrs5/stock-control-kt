package com.br.stock.control.repository

import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.util.filters.supplier.SupplierCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface SupplierRepository: MongoRepository<Supplier, String>, SupplierCustomRepository {
    fun existsByCnpj(cnpj: String): Boolean
}