package com.br.stock.control.integration.stock

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.dto.stock.CreateStockDTO
import com.br.stock.control.model.dto.stock.UpdateStockDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.dto.warehouse.CreateWareDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import com.br.stock.control.model.enum.WareHouseEnum
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.facades.FacadeServices
import com.br.stock.control.util.responses.ResponseBody
import com.br.stock.control.util.responses.ResponseToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class StockControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var facadeRepository: FacadeRepository
    @Autowired private lateinit var facadesServices: FacadeServices

    private val urlStock: String = "/v1/stock"

    @BeforeEach fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.wareHouseRepository.deleteAll()
        this.facadeRepository.productRepository.deleteAll()
        this.facadeRepository.stockRepository.deleteAll()
        this.facadesServices.redisService.deleteAll()
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

        val readTree: JsonNode = objectMapper.readTree(result.response.contentAsString)

        val response: ResponseBody<ResponseToken> = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseBody<ResponseToken>>() {}
        )

        assertThat(response.body.token).isNotBlank
        assertThat(response.body.refreshToken).isNotBlank

        return response.body
    }

    fun createWareHouse(responseToken: ResponseToken): Warehouse {
        val result = mockMvc.perform(
            get("/v1/user/me")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User founded")).andReturn()

        val readTreeNode = objectMapper.readTree(result.response.contentAsString)

        val response2: ResponseBody<UserDTO?> = objectMapper.convertValue(
            readTreeNode,
            object : TypeReference<ResponseBody<UserDTO?>>() {}
        )

        assertThat(response2.body?.id).isNotNull

        val num = Random.nextLong(99999999)
        val dto = CreateWareDTO(
            name = "ware house $num",
            description = "description ware $num",
            addressId = "${UUID.randomUUID()}",
            responsibleUserId = "${response2.body?.id}",
            amount= num,
            capacityCubicMeters = Random.nextDouble(9999999.99),
            type = WareHouseEnum.ANOTHER
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("/v1/ware")
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated).andReturn()

        val readTree: JsonNode = objectMapper.readTree(mvcResult.response.contentAsString)

        val response: ResponseBody<Warehouse> = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseBody<Warehouse>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Warehouse created")
        assertThat(response.body.id).isNotNull

        return response.body
    }

    fun createCategory(token: String): Category {
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

        return body.body
    }

    fun getUser(token: String): UserDTO {
        val mvcResult = mockMvc.perform(
            get("/v1/user/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User founded")).andReturn()

        val response: ResponseBody<UserDTO> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<UserDTO>>() {}
        )

        return response.body
    }

    fun createProduct(responseTokens: ResponseToken): Product {
        val url = "/v1/product"
        val responseCategory: Category = createCategory(responseTokens.token)
        val uuid = "${UUID.randomUUID()}"
        val dto = CreateProductDTO(
            name = "name product $uuid", description= "description $uuid", sku = uuid + uuid,
            barcode = "${Random.nextLong(100000000)}", unitOfMeasure = UnitOfMeasureEnum.UNIT,
            price = BigDecimal.valueOf(Random.nextDouble(99999.99)).setScale(2, RoundingMode.HALF_UP),
            cost = BigDecimal.valueOf(Random.nextDouble(99999.99)).setScale(2, RoundingMode.HALF_UP),
            imageUrl= "", minStockLevel = Random.nextInt(100), maxStockLevel = Random.nextInt(100000) + 100
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("$url/${responseCategory.id}")
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

        return response.body
    }

    fun createStock(responseToken: ResponseToken, productId: String,userId: String, wareHouseId: String): Stock {
        val dto = CreateStockDTO(
            productId = productId,
            quantity = 100,
            responsibleUserId = userId,
            warehouseId = wareHouseId
        )

        val mvcResult = mockMvc.perform(
            post(urlStock)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Stock> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Stock>>() {}
        )

        assertThat(response.message).isEqualTo("Stock created")
        assertThat(response.body.id).isNotBlank.withFailMessage("Id is blank")
        assertThat(response.body.productId).isEqualTo(dto.productId).withFailMessage("Product Id are different")
        assertThat(response.body.quantity).isEqualTo(dto.quantity).withFailMessage("Quantity are different")
        assertThat(response.body.responsibleUserId).isEqualTo(dto.responsibleUserId).withFailMessage("responsibleUserId are different")
        assertThat(response.body.warehouseId).isEqualTo(dto.warehouseId).withFailMessage("Warehouse Id are different")
        assertThat(response.body.isActive).isTrue.withFailMessage("Status active is false")

        return response.body
    }

    @Test
    fun `should create new stock`() {
        val responseToken = createUserAndLog()
        val createWareHouse = createWareHouse(responseToken)
        val createProduct = createProduct(responseToken)
        val user = getUser(responseToken.token)

        val dto = CreateStockDTO(
            productId= createProduct.id,
            quantity = 100,
            responsibleUserId = user.id,
            warehouseId = createWareHouse.id
        )

        val mvcResult = mockMvc.perform(
            post(urlStock)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Stock> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Stock>>() {}
        )

        assertThat(response.message).isEqualTo("Stock created")
        assertThat(response.body.id).isNotBlank.withFailMessage("Id is blank")
        assertThat(response.body.productId).isEqualTo(dto.productId).withFailMessage("Product Id are different")
        assertThat(response.body.quantity).isEqualTo(dto.quantity).withFailMessage("Quantity are different")
        assertThat(response.body.responsibleUserId).isEqualTo(dto.responsibleUserId).withFailMessage("responsibleUserId are different")
        assertThat(response.body.warehouseId).isEqualTo(dto.warehouseId).withFailMessage("Warehouse Id are different")
        assertThat(response.body.isActive).isTrue.withFailMessage("Status active is false")
    }

    @Test
    fun `should get stock`() {
        val responseToken = createUserAndLog()
        val createWareHouse = createWareHouse(responseToken)
        val createProduct = createProduct(responseToken)
        val user = getUser(responseToken.token)
        val stock: Stock = createStock(responseToken, createProduct.id, user.id, createWareHouse.id)

        val mvcResult = mockMvc.perform(
            get(this.urlStock + "/${stock.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Stock> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Stock>>() {}
        )

        assertThat(response.message).isEqualTo("Stock found")
        assertThat(response.body.id).isEqualTo(stock.id).withFailMessage("Id are different")
        assertThat(response.body.productId).isEqualTo(stock.productId).withFailMessage("Product Id are different")
        assertThat(response.body.quantity).isEqualTo(stock.quantity).withFailMessage("Quantity are different")
        assertThat(response.body.responsibleUserId).isEqualTo(stock.responsibleUserId).withFailMessage("responsibleUserId are different")
        assertThat(response.body.warehouseId).isEqualTo(stock.warehouseId).withFailMessage("Warehouse Id are different")
        assertThat(response.body.createdAt).isEqualTo(stock.createdAt).withFailMessage("CreatedAt are different")
        assertThat(response.body.isActive).isTrue.withFailMessage("Status active is false")

    }

    @Test
    fun `should delete stock`() {
        val responseToken = createUserAndLog()
        val createWareHouse = createWareHouse(responseToken)
        val createProduct = createProduct(responseToken)
        val user = getUser(responseToken.token)
        val stock: Stock = createStock(responseToken, createProduct.id, user.id, createWareHouse.id)

        val mvcResult = mockMvc.perform(
            delete(this.urlStock + "/${stock.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Unit> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message).isEqualTo("Stock deleted")
        assertThat(response.body).isInstanceOf(Unit::class.java)
    }

    @Test
    fun `should update stock`() {
        val responseToken = createUserAndLog()
        val createWareHouse = createWareHouse(responseToken)
        val createProduct = createProduct(responseToken)
        val user = getUser(responseToken.token)
        val stock: Stock = createStock(responseToken, createProduct.id, user.id, createWareHouse.id)

        val dto = UpdateStockDTO(
            quantity = Random.nextLong(100000000), responsibleUserId = user.id, warehouseId = createWareHouse.id
        )

        val mvcResult = mockMvc.perform(
            put(urlStock+"/${stock.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Stock> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Stock>>() {}
        )

        assertThat(response.message).isEqualTo("Stock updated")
        assertThat(response.body.id).isEqualTo(stock.id).withFailMessage("Id are different")
        assertThat(response.body.productId).isEqualTo(stock.productId).withFailMessage("Product Id are different")
        assertThat(response.body.quantity).isEqualTo(dto.quantity).withFailMessage("Quantity are different")
        assertThat(response.body.responsibleUserId).isEqualTo(dto.responsibleUserId).withFailMessage("responsibleUserId are different")
        assertThat(response.body.warehouseId).isEqualTo(dto.warehouseId).withFailMessage("Warehouse Id are different")
        assertThat(response.body.isActive).isTrue.withFailMessage("Status active is false")
    }

    @Test
    fun `should change status stock`() {
        val responseToken = createUserAndLog()
        val createWareHouse = createWareHouse(responseToken)
        val createProduct = createProduct(responseToken)
        val user = getUser(responseToken.token)
        val stock: Stock = createStock(responseToken, createProduct.id, user.id, createWareHouse.id)

        val mvcResult = mockMvc.perform(
            put(urlStock+"/${stock.id}/status/active")
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Stock> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Stock>>() {}
        )

        assertThat(response.message).isEqualTo("Status changed")
        assertThat(response.body.id).isEqualTo(stock.id).withFailMessage("Id are different")
        assertThat(response.body.productId).isEqualTo(stock.productId).withFailMessage("Product Id are different")
        assertThat(response.body.quantity).isEqualTo(stock.quantity).withFailMessage("Quantity are different")
        assertThat(response.body.responsibleUserId).isEqualTo(stock.responsibleUserId).withFailMessage("responsibleUserId are different")
        assertThat(response.body.warehouseId).isEqualTo(stock.warehouseId).withFailMessage("Warehouse Id are different")
        assertThat(response.body.isActive).isFalse.withFailMessage("Status active is true")
    }

}