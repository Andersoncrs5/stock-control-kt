package com.br.stock.control.service

import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.repository.SupplierRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class SupplierService(
    private val repository: SupplierRepository
) {

    fun get(id: String): Optional<Supplier> {
        val supplier = this.repository.findById(id)
        return supplier
    }

}