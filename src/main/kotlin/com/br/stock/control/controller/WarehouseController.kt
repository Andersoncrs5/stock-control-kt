package com.br.stock.control.controller

import com.br.stock.control.model.dto.warehouse.CreateWareDTO
import com.br.stock.control.model.dto.warehouse.UpdateWareDTO
import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.WareHouseEnum
import com.br.stock.control.util.facades.FacadeMappers
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.format.annotation.DateTimeFormat
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
import java.util.UUID

@RestController
@RequestMapping("/v1/ware")
class WarehouseController(
    private val facadeServices: FacadeServices,
    private val facadeMappers: FacadeMappers
) {
    @GetMapping("/{id}")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun get(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val ware: Warehouse? = this.facadeServices.wareHouseService.getWareHouse(id);
        if (ware == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not found", request.requestURI,
                    request.method, null
                )
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse found", request.requestURI,
                request.method, ware
            )
        )
    }

    @PostMapping
    @RateLimiter(name = "createApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun create(
        @Valid @RequestBody dto: CreateWareDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        val byName = this.facadeServices.wareHouseService.existsByName(dto.name)
        if (byName) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseBody(
                    LocalDateTime.now(), "Name in use", request.requestURI,
                    request.method, null
                )
            )
        }

        val user = this.facadeServices.userService.get(dto.responsibleUserId)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "User not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val toWarehouse = this.facadeMappers.createWarehouseMapper.toWarehouse(dto)
        toWarehouse.id = UUID.randomUUID().toString()
        val ware = this.facadeServices.wareHouseService.save(toWarehouse)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse created", request.requestURI,
                request.method, ware
            )
        )
    }

    @DeleteMapping("/{id}")
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val ware: Warehouse? = this.facadeServices.wareHouseService.getWareHouse(id)
        if (ware == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not found", request.requestURI,
                    request.method, null
                )
            )
        }

        this.facadeServices.wareHouseService.deleteWareHouse(ware)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse deleted", request.requestURI,
                request.method, null
            )
        )
    }


    @DeleteMapping("/{ids}/many")
    @RateLimiter(name = "deleteApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun deleteMany(
        @PathVariable ids: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        val idList = ids.split(",")

        this.facadeServices.wareHouseService.deleteManyWareHouse(idList)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse many deleted", request.requestURI,
                request.method, null
            )
        )
    }

    @PutMapping("/{id}")
    @RateLimiter(name = "updateApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody dto: UpdateWareDTO,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val ware: Warehouse? = this.facadeServices.wareHouseService.getWareHouse(id)
        if (ware == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val user = this.facadeServices.userService.get(dto.responsibleUserId)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "User not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val update: Warehouse = this.facadeServices.wareHouseService.update(ware, dto)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse updated", request.requestURI,
                request.method, update
            )
        )
    }

    @PutMapping("/{id}/status/active")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun changeStatusActive(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val ware: Warehouse? = this.facadeServices.wareHouseService.getWareHouse(id)
        if (ware == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val active = this.facadeServices.wareHouseService.changeStatusIsActive(ware)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse active status changed", request.requestURI,
                request.method, active
            )
        )
    }

    @PutMapping("/{id}/status/add")
    @RateLimiter(name = "readApiRateLimiter")
    @SecurityRequirement(name = "bearerAuth")
    fun changeStatusCanToAdd(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Warehouse?>> {
        if (id.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseBody(
                    LocalDateTime.now(), "Id is required", request.requestURI,
                    request.method, null
                )
            )
        }

        val ware: Warehouse? = this.facadeServices.wareHouseService.getWareHouse(id)
        if (ware == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseBody(
                    LocalDateTime.now(), "Warehouse not found", request.requestURI,
                    request.method, null
                )
            )
        }

        val active = this.facadeServices.wareHouseService.changeStatusCanToAdd(ware)

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseBody(
                LocalDateTime.now(), "Warehouse can odd status changed", request.requestURI,
                request.method, active
            )
        )
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimiter(name = "readApiRateLimiter")
    fun getAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) addressId: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) responsibleUserId: String?,
        @RequestParam(required = false) minAmount: Long?,
        @RequestParam(required = false) maxAmount: Long?,
        @RequestParam(required = false) minCubicMeters: Double?,
        @RequestParam(required = false) maxCubicMeters: Double?,
        @RequestParam(required = false) type: WareHouseEnum?,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) canToAdd: Boolean?,
        @RequestParam(required = false) createdAtBefore: LocalDateTime?,
        @RequestParam(required = false) createdAtAfter: LocalDateTime?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        request: HttpServletRequest
    ): ResponseEntity<ResponseBody<Page<Warehouse>>> {

        val pageable = PageRequest.of(pageNumber, pageSize)

        val result: Page<Warehouse> = this.facadeServices.wareHouseService.filter(
            name, addressId, description, responsibleUserId,
            minAmount, maxAmount, minCubicMeters, maxCubicMeters,
            type, isActive, canToAdd, createdAtBefore, createdAtAfter, pageable
        )

        return ResponseEntity.ok(
            ResponseBody(
                timestamp = LocalDateTime.now(),
                message = "Warehouses fetched successfully",
                path = request.requestURI,
                method = request.method,
                body = result
            )
        )
    }


}