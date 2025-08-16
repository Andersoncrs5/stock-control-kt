package com.br.stock.control.util.facades

import com.br.stock.control.repository.CategoryRepository
import com.br.stock.control.repository.ProductRepository
import com.br.stock.control.repository.UserRepository
import com.br.stock.control.repository.WarehouseRepository
import org.springframework.stereotype.Service

@Service
class FacadeRepository(
    val userRepository: UserRepository,
    val productRepository: ProductRepository,
    val categoryRepository: CategoryRepository,
    val wareHouseRepository: WarehouseRepository
) {
}