package com.br.stock.control.integration.purchaseOrderItem

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.dto.purchaseOrder.CreateOrderDTO
import com.br.stock.control.model.dto.purchaseOrderItem.CreateOrderItemDTO
import com.br.stock.control.model.dto.purchaseOrderItem.UpdateOrderItemDTO
import com.br.stock.control.model.dto.supplier.CreateSupplierDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.entity.PurchaseOrderItem
import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import com.br.stock.control.model.enum.UnitOfMeasureEnum
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
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
class PurchaseOrderItemControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var facadeRepository: FacadeRepository

    @Autowired private lateinit var facadesServices: FacadeServices

    private val urlOrder: String = "/v1/order"
    private val urlOrderItem: String = "/v1/order-item"
    private val urlSupplier = "/v1/supplier"

    @BeforeEach fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.supplierRepository.deleteAll()
        this.facadeRepository.categoryRepository.deleteAll()
        this.facadeRepository.purchaseOrderRepository.deleteAll()
        this.facadeRepository.purchaseOrderItemRepository.deleteAll()
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

    fun getUser(token: ResponseToken): UserDTO {
        val mvcResult = mockMvc.perform(
            get("/v1/user/me")
                .header("Authorization", "Bearer ${token.token}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User founded")).andReturn()

        val response: ResponseBody<UserDTO> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<UserDTO>>() {}
        )

        return response.body
    }

    fun createCategory(token: String): Category {
        val categoryUrl = "/v1/category"
        val dtoCategory = CreateCategoryDTO(
            name = "category 1" + Random.nextLong(100000000),
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

    fun createSupplier(responseToken: ResponseToken, user: UserDTO, categories: MutableList<String>): Supplier {

        val dto = CreateSupplierDTO(
            cnpj = Random.nextLong(10000000000).toString(),
            nameEnterprise = "lorem enterprise",
            notes = "some thing",
            type = SupplierTypeEnum.MEI,
            rating = Random.nextInt(1,11),
            categoriesId = categories,
            createdBy = "",
        )

        val mvcResult = mockMvc.perform(
            post(urlSupplier)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Supplier> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Supplier>>() {}
        )

        assertThat(response.message).isEqualTo("Supplier created").withFailMessage("Message is different")
        assertThat(response.body.userId).isEqualTo(user.id).withFailMessage("User Id is different")

        return response.body
    }

    fun createOrder(
        supplier1: Supplier, responseToken: ResponseToken
    ): PurchaseOrder {
        val dto = CreateOrderDTO(
            supplierId = supplier1.userId as String,
            expectedDeliveryDate = LocalDate.now().plusMonths(3),
            currency = CurrencyEnum.BTC,
            notes = "lorem note"
        )

        val mvcResult = mockMvc.perform(
            post(this.urlOrder)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<PurchaseOrder> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrder>>() {}
        )

        assertThat(response.message).isEqualTo("Order created")
            .withFailMessage("Message are different")

        return response.body
    }

    fun createProduct(responseTokens: ResponseToken): Product {
        val url: String = "/v1/product"

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

    fun createOrderItem(
        product: Product,
        order: PurchaseOrder,
        responseToken: ResponseToken
    ): PurchaseOrderItem {
        val dto = CreateOrderItemDTO(
            productId = product.id,
            quantity = 100,
            unitPrice = BigDecimal.valueOf(10000.0).setScale(2, RoundingMode.HALF_UP),
            expectedQuantity = 100,
            backOrderedQuantity = 0,
            receivedQuantity = 100
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("$urlOrderItem/${order.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrderItem>>() {}
        )

        assertThat(response.message).isEqualTo("Order item created").withFailMessage("Return message is different")
        assertThat(response.body.purchaseOrderId)
            .isEqualTo(order.id)
            .withFailMessage("Purchase Order Id are different")

        return response.body
    }

    @Test fun `should create a order item`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val category = createCategory(responseToken.token)
        val supplier = createSupplier(responseToken, userDTO, listOf(category.id) as MutableList<String>)

        val responseToken1 = createUserAndLog()
        getUser(responseToken)
        val product = createProduct(responseToken1)
        val createOrder = createOrder(supplier, responseToken1)

        val dto = CreateOrderItemDTO(
            productId = product.id,
            quantity = 100,
            unitPrice = BigDecimal.valueOf(10000.0).setScale(2, RoundingMode.HALF_UP),
            expectedQuantity = 100,
            backOrderedQuantity = 0,
            receivedQuantity = 100
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("$urlOrderItem/${createOrder.id}")
                .header("Authorization", "Bearer ${responseToken1.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrderItem>>() {}
        )

        assertThat(response.message).isEqualTo("Order item created").withFailMessage("Return message is different")
        assertThat(response.body.purchaseOrderId)
            .isEqualTo(createOrder.id)
            .withFailMessage("Purchase Order Id are different")

        assertThat(response.body.productId)
            .isEqualTo(product.id)
            .withFailMessage("Product Id are different")

        assertThat(response.body.quantity)
            .isEqualTo(dto.quantity)
            .withFailMessage("quantity are different")

        assertThat(response.body.unitPrice)
            .isEqualTo(dto.unitPrice)
            .withFailMessage("unitPrice are different")

        assertThat(response.body.expectedQuantity)
            .isEqualTo(dto.expectedQuantity)
            .withFailMessage("expectedQuantity are different")

        assertThat(response.body.backOrderedQuantity)
            .isEqualTo(dto.backOrderedQuantity)
            .withFailMessage("backOrderedQuantity are different")

        assertThat(response.body.receivedQuantity)
            .isEqualTo(dto.receivedQuantity)
            .withFailMessage("receivedQuantity are different")

    }

    @Test fun `should get a orderItem`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val category = createCategory(responseToken.token)
        val supplier = createSupplier(responseToken, userDTO, listOf(category.id) as MutableList<String>)

        val responseToken1 = createUserAndLog()
        getUser(responseToken)
        val product = createProduct(responseToken1)
        val createOrder = createOrder(supplier, responseToken1)
        val orderItem = createOrderItem(product, createOrder, responseToken1)

        val mvcResult = mockMvc.perform(
            get(this.urlOrderItem + "/${orderItem.id}")
                .header("Authorization", "Bearer ${responseToken1.token}"))
            .andExpect(status().isOk).andReturn()

        val response = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrderItem>>() {}
        )

        assertThat(response.message)
            .isEqualTo("PurchaseOrderItem found")
            .withFailMessage("Return message is different")

        assertThat(response.body.id)
            .isEqualTo(orderItem.id)
            .withFailMessage("Id are different")

        assertThat(response.body.productId)
            .isEqualTo(orderItem.productId)
            .withFailMessage("Product id are different")

        assertThat(response.body.quantity)
            .isEqualTo(orderItem.quantity)
            .withFailMessage("Quantity are different")

        assertThat(response.body.expectedQuantity)
            .isEqualTo(orderItem.expectedQuantity)
            .withFailMessage("expectedQuantity are different")

    }

    @Test fun `should delete a orderItem`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val category = createCategory(responseToken.token)
        val supplier = createSupplier(responseToken, userDTO, listOf(category.id) as MutableList<String>)

        val responseToken1 = createUserAndLog()
        getUser(responseToken)
        val product = createProduct(responseToken1)
        val createOrder = createOrder(supplier, responseToken1)
        val orderItem = createOrderItem(product, createOrder, responseToken1)

        val mvcResult = mockMvc.perform(
            delete(this.urlOrderItem + "/${orderItem.id}")
                .header("Authorization", "Bearer ${responseToken1.token}"))
            .andExpect(status().isOk).andReturn()

        val response = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrderItem>>() {}
        )

        assertThat(response.message)
            .isEqualTo("PurchaseOrderItem deleted")
            .withFailMessage("Return message is different")

        mockMvc.perform(
            get(this.urlOrderItem + "/${orderItem.id}")
                .header("Authorization", "Bearer ${responseToken1.token}"))
            .andExpect(status().isNotFound).andReturn()
    }

    @Test fun `should update orderItem`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val category = createCategory(responseToken.token)
        val supplier = createSupplier(responseToken, userDTO, listOf(category.id) as MutableList<String>)

        val responseToken1 = createUserAndLog()
        getUser(responseToken1)
        val product = createProduct(responseToken1)
        val createOrder = createOrder(supplier, responseToken1)
        val orderItem = createOrderItem(product, createOrder, responseToken1)
        
        val dto = UpdateOrderItemDTO(
            quantity = 99999,
            unitPrice = BigDecimal.valueOf(99999.999),
            expectedQuantity = 99999,
            backOrderedQuantity = 0,
            receivedQuantity = 99999
        )

        val mvcResult: MvcResult = mockMvc.perform(
            put("$urlOrderItem/${orderItem.id}")
                .header("Authorization", "Bearer ${responseToken1.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrderItem>>() {}
        )

        assertThat(response.body.id)
            .isEqualTo(orderItem.id)
            .withFailMessage("Ids are different")

        assertThat(response.message)
            .isEqualTo("Purchase Order Item updated with successfully!")
            .withFailMessage("Message are different")

    }
    
}