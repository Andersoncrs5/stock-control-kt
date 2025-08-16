package com.br.stock.control.util.facades

import com.br.stock.control.util.mappers.address.CreateAddressMapper
import com.br.stock.control.util.mappers.category.CreateCategoryMapper
import com.br.stock.control.util.mappers.category.UpdateCategoryMapper
import com.br.stock.control.util.mappers.product.CreateProductMapper
import com.br.stock.control.util.mappers.user.UserDTOMapper
import com.br.stock.control.util.mappers.warehouse.CreateWarehouseMapper
import org.springframework.stereotype.Service

@Service
class FacadeMappers(
    val userDTOMapper: UserDTOMapper,
    val createProductMapper: CreateProductMapper,
    val createCategoryMapper: CreateCategoryMapper,
    val updateCategoryMapper: UpdateCategoryMapper,
    val createWarehouseMapper: CreateWarehouseMapper,
    val createAddressMapper: CreateAddressMapper,
) {
}