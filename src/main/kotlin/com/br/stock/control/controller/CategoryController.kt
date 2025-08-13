package com.br.stock.control.controller

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.Duration
import java.util.Optional
import java.util.UUID

@RestController
@RequestMapping("/v1/category")
class CategoryController(
    private val facade: FacadeServices,
    private val facadesMappers: FacadeMappers
) {

    private val logger = LoggerFactory.getLogger(CategoryController::class.java)

    @GetMapping("/{id}")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ResponseBody<Category>> {
        logger.debug("Getting category by id $id")
        if (id.isBlank()) {
            logger.debug("Id came null")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required!",
                    request.requestURI, request.method, null
                )
            )
        }

        logger.debug("Searching in the db")
        val category: Optional<Category> = this.facade.category.get(id)

        if (category.isEmpty) {
            logger.debug("Category  not found")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Category not found!",
                    request.requestURI, request.method, null
                )
            )
        }

        logger.debug("Category found")
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Category found!",
                request.requestURI, request.method, category.get()
            )
        )
    }

    @PostMapping
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun create(
        @Valid @RequestBody dto: CreateCategoryDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Category>> {
        logger.debug("Creating new category")
        val category: Category = this.facadesMappers.createCategoryMapper.toCategory(dto)

        val byName: Optional<Category> = this.facade.category.getByName(category.name)

        if (byName.isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    LocalDateTime.now(), "Category name already exists!",
                    request.requestURI, request.method, null
                )
            )
        }

        category.id = UUID.randomUUID().toString()
        val result: Category = this.facade.category.save(category)

        logger.debug("Returning neew category")
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(), "Category created!",
                request.requestURI, request.method, result
            )
        )
    }

    @GetMapping
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun getAll(request: HttpServletRequest): ResponseEntity<ResponseBody<List<Category>>> {
        val cached: List<Category>? = facade.redisService.get<List<Category>>("categories")

        val categories: List<Category> = cached ?: facade.category.getAll().also {
            facade.redisService.set("categories", it, Duration.ofMinutes(10))
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Categories found!",
                path = request.requestURI,
                method = request.method,
                body = categories
            )
        )
    }


}