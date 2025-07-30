package com.br.stock.control.util.facades

import com.br.stock.control.repository.ProductRepository
import com.br.stock.control.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class FacadeRepository(
    val userRepository: UserRepository,
    val productRepository: ProductRepository
) {
}