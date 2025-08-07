package com.br.stock.control.controller

import com.br.stock.control.model.entity.Product
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.mappers.product.CreateProductMapper
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/product")
class ProductController(
    private val facades: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun get(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ResponseBody<Product>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody<Product>(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val product: Product? = this.facades.productService.getProduct(id)

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody<Product>(
                    LocalDateTime.now(), "Product not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody<Product>(
                LocalDateTime.now(), "Product founded", request.requestURI,
                request.method, product
            )
        )
    }

    @PostMapping("/{categoryId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "createApiRateLimiter")
    fun create(
        @PathVariable categoryId: String,
        @Valid @RequestBody dto: CreateProductMapper,
        request: HttpServletRequest
    ) {

    }

}