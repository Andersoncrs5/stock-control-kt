package com.br.stock.control.util.mappers.supplier

import com.br.stock.control.model.dto.supplier.CreateSupplierDTO
import com.br.stock.control.model.entity.Supplier
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateSupplierMapper(
    private val mapper: Mapper
) {

    fun toDTO(supplier: Supplier): CreateSupplierMapper {
        return this.mapper.map(supplier, CreateSupplierMapper::class.java)
    }

    fun toSupplier(dto: CreateSupplierDTO): Supplier {
        return this.mapper.map(dto, Supplier::class.java)
    }

}