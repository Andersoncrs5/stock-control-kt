package com.br.stock.control.controller

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.category.UpdateCategoryDTO
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.Duration
import java.time.LocalDate
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
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Category?>> {
        logger.debug("Getting category by id $id")
        if (id.isBlank()) {
            logger.debug("Id came null the get category")
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
    ): ResponseEntity<ResponseBody<Category?>> {
        logger.debug("Creating new category")
        val category: Category = this.facadesMappers.createCategoryMapper.toCategory(dto)

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
    fun getAll(
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<List<Category>>> {
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

    @DeleteMapping("/{id}")
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<String?>> {
        logger.debug("Deleting category by id $id")
        if (id.isBlank()) {
            logger.debug("Id came null")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required!",
                    request.requestURI, request.method, null
                )
            )
        }

        logger.debug("Searching in the db to delete")
        val category: Optional<Category> = this.facade.category.get(id)

        if (category.isEmpty) {
            logger.debug("Category not found")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Category not found!",
                    request.requestURI, request.method, null
                )
            )
        }

        this.facade.category.delete(category.get())

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Category deleted!",
                request.requestURI, request.method, null
            )
        )
    }

    @DeleteMapping("/{ids}/many")
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun deleteMany(
        @PathVariable ids: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<String?>> {
        val idList = ids.split(",")
        logger.debug("Deleting many category by id {}", idList)
        this.facade.category.deleteMany(idList)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Categories deleted!",
                request.requestURI, request.method, null
            )
        )
    }

    @PutMapping("/{id}")
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody dto: UpdateCategoryDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Category?>> {
        logger.debug("Updating category by id $id")
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required!",
                    request.requestURI, request.method, null
                )
            )
        }

        logger.debug("Searching in the db to update")
        val category: Optional<Category> = this.facade.category.get(id)

        if (category.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Category not found!",
                    request.requestURI, request.method, null
                )
            )
        }

        if (category.get().name != dto.name) {

            val optional = this.facade.category.getByName(dto.name)
            if (optional.isPresent) {
                return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseBody(
                        LocalDateTime.now(),
                        "Category name already exists!",
                        request.requestURI,
                        request.method,
                        null
                    )
                )
            }

        }

        val update = this.facade.category.update(category.get(), dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(),
                "Category updated!",
                request.requestURI,
                request.method,
                update
            )
        )
    }

    @GetMapping("/filter")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun filter(
        @RequestParam name: String,
        @RequestParam description: String,
        @RequestParam active: Boolean,
        @RequestParam createdAtBefore: LocalDate,
        @RequestParam createdAtAfter: LocalDate,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<Category>>> {

        val pageable: Pageable = PageRequest.of(page, size)

        val categories: Page<Category> = this.facade.category.filter(
            name, description, active,
            createdAtBefore, createdAtAfter,
            pageble = pageable
        )

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "",
                request.requestURI, request.method, categories
            )
        )
    }

    @PutMapping("/{id}/status")
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun changeStatus(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Category?>> {
        logger.debug("Getting category by id $id to change status")

        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required!",
                    request.requestURI, request.method, null
                )
            )
        }

        val category: Optional<Category> = this.facade.category.get(id)
        if (category.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Category not found!",
                    request.requestURI, request.method, null
                )
            )
        }

        val change: Category = this.facade.category.changeStatusActive(category.get())

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(),
                if (change.active) "Category actived" else "Category disabled",
                request.requestURI, request.method, change
            )
        )
    }


}