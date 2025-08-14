package com.br.stock.control.controller

import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Product
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.mappers.product.CreateProductMapper
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@RestController
@RequestMapping("/v1/product")
class ProductController(
    private val facades: FacadeServices,
    private val facadeMappers: FacadeMappers
) {

    private val logger = LoggerFactory.getLogger(ProductController::class.java)

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
        @Valid @RequestBody dto: CreateProductDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Product>> {
        val bySku = this.facades.productService.getBySku(dto.sku)
        if(bySku.isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    LocalDateTime.now(), "Sku ${dto.sku} in use!",
                    request.requestURI, request.method, null
                )
            )
        }

        val byBarcode = this.facades.productService.getByBarcode(dto.barcode)
        if(byBarcode.isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    LocalDateTime.now(), "Barcode ${dto.barcode} in use!",
                    request.requestURI, request.method, null
                )
            )
        }

        if (categoryId.isBlank()) {
            logger.debug("Id came null")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required!",
                    request.requestURI, request.method, null
                )
            )
        }

        val category: Optional<Category> = this.facades.category.get(categoryId)

        if (category.isEmpty) {
            logger.debug("Category  not found")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Category not found!",
                    request.requestURI, request.method, null
                )
            )
        }

        val product: Product = this.facadeMappers.createProductMapper.toProduct(dto)
        product.id = UUID.randomUUID().toString()
        product.categoryId = category.get().id

        val result: Product = this.facades.productService.save(product)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(LocalDateTime.now(),
                "Product created", request.requestURI,
                request.method, result
            )
        )
    }

}