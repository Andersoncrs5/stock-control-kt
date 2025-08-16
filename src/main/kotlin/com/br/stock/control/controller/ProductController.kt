package com.br.stock.control.controller

import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.dto.product.UpdateProductDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
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
    fun get(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ResponseBody<Product?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val product: Product? = this.facades.productService.get(id)

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
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
    ): ResponseEntity<ResponseBody<Product?>> {
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

    @DeleteMapping("/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "deleteApiRateLimiter")
    fun delete(@PathVariable productId: String, request: HttpServletRequest): ResponseEntity<ResponseBody<Product?>> {
        val product: Product? = this.facades.productService.get(productId)

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not found", request.requestURI,
                    request.method, null
                )
            )
        }

        this.facades.productService.delete(product)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Product deleted", request.requestURI,
                request.method, null
            )
        )
    }

    @DeleteMapping("/{ids}/many")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "deleteApiRateLimiter")
    fun deleteMany(@PathVariable ids: String, request: HttpServletRequest): ResponseEntity<ResponseBody<String?>> {
        val idList = ids.split(",")
        logger.debug("Deleting many product by id {}", idList)
        this.facades.productService.deleteMany(idList)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Products deleted!",
                request.requestURI, request.method, null
            )
        )
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "updateApiRateLimiter")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody dto: UpdateProductDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Product?>> {
        val product: Product? = this.facades.productService.get(id)

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not found", request.requestURI,
                    request.method, null
                )
            )
        }

        if (product.sku != dto.sku) {
            val bySku = this.facades.productService.getBySku(dto.sku)
            if(bySku.isPresent) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseBody(
                        LocalDateTime.now(), "Sku ${dto.sku} in use!",
                        request.requestURI, request.method, null
                    )
                )
            }
        }

        if (product.barcode != dto.barcode) {
            val byBarcode = this.facades.productService.getByBarcode(dto.barcode)
            if(byBarcode.isPresent) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseBody(
                        LocalDateTime.now(), "Barcode ${dto.barcode} in use!",
                        request.requestURI, request.method, null
                    )
                )
            }
        }

        val update: Product = this.facades.productService.update(product, dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Product updated", request.requestURI,
                request.method, update
            )
        )
    }

    @PutMapping("/{id}/status")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "updateApiRateLimiter")
    fun change(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ResponseBody<Product?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val product: Product? = this.facades.productService.get(id)

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Product not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val status = this.facades.productService.changeStatus(product)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Status changed!", request.requestURI,
                request.method, status
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) sku: String?,
        @RequestParam(required = false) barcode: String?,
        @RequestParam(required = false) categoryId: String?,
        @RequestParam(required = false) unitOfMeasure: UnitOfMeasureEnum?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) minCost: BigDecimal?,
        @RequestParam(required = false) maxCost: BigDecimal?,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) createdAtBefore: LocalDate?,
        @RequestParam(required = false) createdAtAfter: LocalDate?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<Product>>> {

        val result: Page<Product> = facades.productService.findAll(
            name, sku, barcode, categoryId, unitOfMeasure,
            minPrice, maxPrice, minCost, maxCost, createdAtBefore, createdAtAfter, isActive,
            pageNumber, pageSize
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Products fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }


}