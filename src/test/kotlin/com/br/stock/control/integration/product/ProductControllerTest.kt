package com.br.stock.control.integration.product

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.dto.product.UpdateProductDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import com.br.stock.control.util.responses.ResponseToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.math.RoundingMode

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var facadesServices: FacadeServices

    @Autowired
    private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/product"

    @BeforeEach
    fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.productRepository.deleteAll()
        this.facadeRepository.categoryRepository.deleteAll()
        facadesServices.redisService.deleteAll()
    }

    fun createUserAndLog(): ResponseToken {
        val num = Random.nextLong(1000000000)
        val password = UUID.randomUUID().toString()
        val dto = RegisterUserDTO(
            name = "user${num}",
            email = "user${num}@gmail.com",
            fullName = "user $num",
            passwordHash = password
        )

        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)

        val dtoLog = LoginUserDTO(dto.name, password)

        val result: MvcResult = mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoLog))
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<ResponseToken> = objectMapper.convertValue(
            objectMapper.readTree(result.response.contentAsString),
            object : TypeReference<ResponseBody<ResponseToken>>() {}
        )

        assertThat(response.body.token).isNotBlank
        assertThat(response.body.refreshToken).isNotBlank

        return response.body
    }

    fun createCategory(token: String): ResponseBody<Category> {
        val categoryUrl = "/v1/category"
        val dtoCategory = CreateCategoryDTO(
            name = "category 1" + Random.nextLong(10000),
            description = "description 1"
        )

        val result1 = mockMvc.perform(
            post(categoryUrl)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoCategory))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andReturn()

        val node: JsonNode = objectMapper.readTree(result1.response.contentAsString)

        val body = objectMapper.convertValue(
            node,
            object : TypeReference<ResponseBody<Category>>() {}
        )

        assertThat(body).isNotNull
        assertThat(body.body.id).isNotNull
        assertThat(body.body.name).isEqualTo(dtoCategory.name)
        assertThat(body.body.description).isEqualTo(dtoCategory.description)

        return body
    }

    fun createProduct(responseTokens: ResponseToken): ResponseBody<Product> {
        val responseCategory = createCategory(responseTokens.token)
        val uuid = "${UUID.randomUUID()}"
        val dto = CreateProductDTO(
            name = "name product $uuid", description= "description $uuid", sku = uuid + uuid,
            barcode = "${Random.nextLong(100000000)}", unitOfMeasure = UnitOfMeasureEnum.UNIT,
            price = BigDecimal.valueOf(Random.nextDouble(99999.99)).setScale(2, RoundingMode.HALF_UP),
            cost = BigDecimal.valueOf(Random.nextDouble(99999.99)).setScale(2, RoundingMode.HALF_UP),
            imageUrl= "", minStockLevel = Random.nextInt(100), maxStockLevel = Random.nextInt(100000) + 100
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("$url/${responseCategory.body.id}")
                .header("Authorization", "Bearer ${responseTokens.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Product> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Product>>() {}
        )

        assertThat(response.message).isEqualTo("Product created")
        assertThat(response.body.id).isNotNull

        return response
    }

    @Test
    fun `should create new product`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val responseCategory = createCategory(responseTokens.token)
        val uuid = "${UUID.randomUUID()}"

        val dto = CreateProductDTO(
            name = "name product $uuid", description= "description $uuid", sku = uuid,
            barcode = "${Random.nextLong(100000000)}", unitOfMeasure = UnitOfMeasureEnum.UNIT,
            price = BigDecimal.valueOf(Random.nextDouble(99999.99)).setScale(2, RoundingMode.HALF_UP),
            cost = BigDecimal.valueOf(Random.nextDouble(99999.99)).setScale(2, RoundingMode.HALF_UP),
            imageUrl= "", minStockLevel = Random.nextInt(100), maxStockLevel = Random.nextInt(100000) + 100
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("$url/${responseCategory.body.id}")
                .header("Authorization", "Bearer ${responseTokens.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Product> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Product>>() {}
        )

        assertThat(response.message).isEqualTo("Product created")
        assertThat(response.body.id).isNotNull
        assertThat(response.body.name).isEqualTo(dto.name)
        assertThat(response.body.description).isEqualTo(dto.description)
        assertThat(response.body.sku).isEqualTo(dto.sku)
        assertThat(response.body.price).isEqualTo(dto.price)
        assertThat(response.body.cost).isEqualTo(dto.cost)
        assertThat(response.body.minStockLevel).isEqualTo(dto.minStockLevel)
        assertThat(response.body.maxStockLevel).isEqualTo(dto.maxStockLevel)
        assertThat(response.body.categoryId)
            .isEqualTo(responseCategory.body.id).withFailMessage("Categories ids are different")

    }

    @Test
    fun `should get product`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val responseProduct: ResponseBody<Product> = createProduct(responseTokens)

        val mvcResult: MvcResult = mockMvc.perform(
            get("$url/${responseProduct.body.id}")
                .header("Authorization", "Bearer ${responseTokens.token}")
        )
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Product> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Product>>() {})

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Product founded")
        assertThat(response.body.id).isEqualTo(responseProduct.body.id).withFailMessage("Id are different")
        assertThat(response.body.sku).isEqualTo(responseProduct.body.sku).withFailMessage("Id are different")
        assertThat(response.body.barcode).isEqualTo(responseProduct.body.barcode).withFailMessage("Id are different")
    }

    @Test
    fun `should delete product`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val responseProduct: ResponseBody<Product> = createProduct(responseTokens)

        val mvcResult: MvcResult = mockMvc.perform(
            delete("$url/${responseProduct.body.id}")
                .header("Authorization", "Bearer ${responseTokens.token}")
        )
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Product> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Product>>() {}
        )

        assertThat(response.body).isNull()
        assertThat(response.message).isEqualTo("Product deleted").withFailMessage("Message are different")
    }

    @Test
    fun `should delete many products`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val products = mutableListOf<ResponseBody<Product>>()

        repeat(3) { products.add(createProduct(responseTokens)) }

        val ids = products.joinToString(",") { it.body.id }

        val mvcResult: MvcResult = mockMvc.perform(
            delete("$url/$ids/many")
                .header("Authorization", "Bearer ${responseTokens.token}")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<String> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<String>>() {}
        )

        assertThat(response.body).isNull()
        assertThat(response.message).isEqualTo("Products deleted!")
    }

    @Test
    fun `should update product`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val responseProduct: ResponseBody<Product> = createProduct(responseTokens)

        val dto = UpdateProductDTO(
            name = "product updated", description = "description updated",
            sku = responseProduct.body.sku, barcode = responseProduct.body.barcode,
            unitOfMeasure = UnitOfMeasureEnum.UNIT,
            price = BigDecimal.valueOf(Random.nextDouble(9999.99)).setScale(2, RoundingMode.HALF_UP),
            cost = BigDecimal.valueOf(Random.nextDouble(999.99)).setScale(2, RoundingMode.HALF_UP),
            imageUrl = "", minStockLevel = Random.nextInt(99),
            maxStockLevel = Random.nextInt(9999) + 99, locationSpecificStock = mapOf()
        )

        val mvcResult = mockMvc.perform(
            put("$url/${responseProduct.body.id}")
                .header("Authorization", "Bearer ${responseTokens.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Product?> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Product?>>() {}
        )

        assertThat(response).isNotNull
        assertThat(response.message).isEqualTo("Product updated")
        assertThat(response.body).isNotNull
        assertThat(response.body!!.id).isEqualTo(responseProduct.body.id)
        assertThat(response.body!!.name).isEqualTo(dto.name)
        assertThat(response.body!!.description).isEqualTo(dto.description)
        assertThat(response.body!!.sku).isEqualTo(dto.sku)
        assertThat(response.body!!.barcode).isEqualTo(dto.barcode)
        assertThat(response.body!!.unitOfMeasure).isEqualTo(dto.unitOfMeasure)
        assertThat(response.body!!.price).isEqualTo(dto.price)
        assertThat(response.body!!.cost).isEqualTo(dto.cost)
        assertThat(response.body!!.minStockLevel).isEqualTo(dto.minStockLevel)
        assertThat(response.body!!.maxStockLevel).isEqualTo(dto.maxStockLevel)
    }

    @Test
    fun `should change status product`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val responseProduct: ResponseBody<Product> = createProduct(responseTokens)

        val mvcResult = mockMvc.perform(
            put(this.url + "/${responseProduct.body.id}/status")
                .header("Authorization", "Bearer ${responseTokens.token}")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Product?> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Product?>>() {}
        )

        assertThat(response).isNotNull
        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Status changed!")
        assertThat(response.body!!.id).isEqualTo(responseProduct.body.id)
        assertThat(response.body!!.isActive).isNotEqualTo(responseProduct.body.isActive)
    }

}