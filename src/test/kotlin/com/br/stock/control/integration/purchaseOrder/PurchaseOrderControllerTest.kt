package com.br.stock.control.integration.purchaseOrder

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.purchaseOrder.CreateOrderDTO
import com.br.stock.control.model.dto.supplier.CreateSupplierDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.StatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import com.br.stock.control.util.facades.FacadeRepository
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
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseOrderControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var facadeRepository: FacadeRepository

    private val urlOrder: String = "/v1/order"
    private val urlSupplier = "/v1/supplier"

    @BeforeEach fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.supplierRepository.deleteAll()
        this.facadeRepository.purchaseOrderRepository.deleteAll()
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

    @Test fun `should create new order`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken1: ResponseToken = createUserAndLog()

        getUser(responseToken.token)
        val user1: UserDTO = getUser(responseToken1.token)
        
        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier1: Supplier = createSupplier(responseToken1, user1, categories)
        
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

        assertThat(response.body.id).isNotNull.isNotBlank
            .withFailMessage("Id is null or blank")

        assertThat(response.body.supplierId).isEqualTo(dto.supplierId)
            .withFailMessage("Supplier Id are different")

        assertThat(response.body.expectedDeliveryDate).isEqualTo(dto.expectedDeliveryDate)
            .withFailMessage("expectedDeliveryDate are different")

        assertThat(response.body.currency).isEqualTo(dto.currency)
            .withFailMessage("currency are different")

        assertThat(response.body.notes).isEqualTo(dto.notes)
            .withFailMessage("notes are different")

        assertThat(response.body.status).isEqualTo(StatusEnum.PENDING)
            .withFailMessage("status is different of PENDING")
    }

    @Test fun `should get order`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken1: ResponseToken = createUserAndLog()

        getUser(responseToken.token)
        val user1: UserDTO = getUser(responseToken1.token)

        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier1: Supplier = createSupplier(responseToken1, user1, categories)
        val order = createOrder(supplier1, responseToken1)

        val mvcResult = mockMvc.perform(get(this.urlOrder+"/${order.id}")
            .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()
            )
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<PurchaseOrder> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrder>>() {}
        )

        assertThat(response.message).isEqualTo("Order found")
            .withFailMessage("Message is different")

        assertThat(response.body.id).isEqualTo(order.id)
            .withFailMessage("Id are different")

        assertThat(response.body.status).isEqualTo(order.status)
            .withFailMessage("Status are different")

        assertThat(response.body.supplierId).isEqualTo(order.supplierId)
            .withFailMessage(" are different")

    }

    @Test fun `should delete order`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken1: ResponseToken = createUserAndLog()

        getUser(responseToken.token)
        val user1: UserDTO = getUser(responseToken1.token)

        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier1: Supplier = createSupplier(responseToken1, user1, categories)
        val order = createOrder(supplier1, responseToken1)

        val mvcResult = mockMvc.perform(delete(this.urlOrder+"/${order.id}")
            .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Unit> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message).isEqualTo("Order deleted")
            .withFailMessage("Message is different")

        mockMvc.perform(get(this.urlOrder+"/${order.id}")
            .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Order not found"))
            .andExpect(jsonPath("$.body").isEmpty)
    }

    @Test fun `should change status to approved`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken1: ResponseToken = createUserAndLog()

        val user: UserDTO = getUser(responseToken.token)
        val user1: UserDTO = getUser(responseToken1.token)

        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier1: Supplier = createSupplier(responseToken1, user1, categories)
        val order: PurchaseOrder = createOrder(supplier1, responseToken1)

        val mvcResult = mockMvc.perform(
            put(this.urlOrder + "/${order.id}/status/approved")
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andReturn()

        val response: ResponseBody<PurchaseOrder> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrder>>() {}
        )

        assertThat(response.message).isEqualTo("Status change to approved")
            .withFailMessage("Message is different")

        assertThat(response.body.id).isEqualTo(order.id)
            .withFailMessage("id are different")

        assertThat(response.body.status).isEqualTo(StatusEnum.APPROVED)
            .withFailMessage("Status are different")

        assertThat(response.body.approvedByUserId).isEqualTo(user.id)
            .withFailMessage("approvedByUserId is different of user id: ${user.id}")

        assertThat(response.body.approveAt).isEqualTo(LocalDate.now())
            .withFailMessage("approveAt is different of ${LocalDate.now()}")

        mockMvc.perform(get(this.urlOrder+"/${order.id}")
            .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Order found"))
    }

    @Test fun `should change status to cancel`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken1: ResponseToken = createUserAndLog()

        val user: UserDTO = getUser(responseToken.token)
        val user1: UserDTO = getUser(responseToken1.token)

        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier1: Supplier = createSupplier(responseToken1, user1, categories)
        val order: PurchaseOrder = createOrder(supplier1, responseToken1)

        val reason = "No stock"

        val mvcResult = mockMvc.perform(
            put(this.urlOrder + "/${order.id}/status/cancel")
                .param("reason", reason)
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andReturn()

        val response: ResponseBody<PurchaseOrder> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrder>>() {}
        )

        assertThat(response.message).isEqualTo("Status change to cancel")
            .withFailMessage("Message is different")

        assertThat(response.body.id).isEqualTo(order.id)
            .withFailMessage("id are different")

        assertThat(response.body.status).isEqualTo(StatusEnum.CANCELLED)
            .withFailMessage("Status are different")

        assertThat(response.body.canceledByUserId).isEqualTo(user.id)
            .withFailMessage("canceledByUserId is different of user id: ${user.id}")

        assertThat(response.body.canceledAt).isEqualTo(LocalDate.now())
            .withFailMessage("canceledAt is different of ${LocalDate.now()}")

        assertThat(response.body.reasonCancel).isEqualTo(reason)
            .withFailMessage("Cancel reason is different of $reason")

        mockMvc.perform(get(this.urlOrder+"/${order.id}")
            .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Order found"))
            .andExpect(jsonPath("$.body.reasonCancel").value(reason))
            .andExpect(jsonPath("$.body.canceledByUserId").value(user.id))
    }

    @Test fun `should change status to receive`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken1: ResponseToken = createUserAndLog()

        getUser(responseToken.token)
        val user1: UserDTO = getUser(responseToken1.token)

        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier1: Supplier = createSupplier(responseToken1, user1, categories)
        val order: PurchaseOrder = createOrder(supplier1, responseToken1)

        val mvcResult = mockMvc.perform(
            put(this.urlOrder + "/${order.id}/${LocalDate.now()}/status/receive")
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andReturn()

        val response: ResponseBody<PurchaseOrder> = objectMapper.readValue(
            mvcResult.response.contentAsString,
            object : TypeReference<ResponseBody<PurchaseOrder>>() {}
        )

        assertThat(response.message).isEqualTo("Status change to receive")
            .withFailMessage("Message is different")

        assertThat(response.body.id).isEqualTo(order.id)
            .withFailMessage("id are different")

        assertThat(response.body.status).isEqualTo(StatusEnum.RECEIVED)
            .withFailMessage("Status are different")

        assertThat(response.body.deliveryDate).isEqualTo(LocalDate.now())
            .withFailMessage("deliveryDate is different of ${LocalDate.now()}")

        mockMvc.perform(get(this.urlOrder+"/${order.id}")
            .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Order found"))
            .andExpect(jsonPath("$.body.id").value(order.id))
    }

}