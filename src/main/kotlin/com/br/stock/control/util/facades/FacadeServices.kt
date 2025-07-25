package com.br.stock.control.util.facades

import com.br.stock.control.config.security.service.TokenService
import com.br.stock.control.service.ProductService
import com.br.stock.control.service.RedisService
import com.br.stock.control.service.UserService
import com.br.stock.control.service.WareHouseService
import org.springframework.stereotype.Service

@Service
class FacadeServices(
    val userService: UserService,
    val productService: ProductService,
    val wareHouseService: WareHouseService,
    val tokenService: TokenService,
    val redisService: RedisService
) {
}