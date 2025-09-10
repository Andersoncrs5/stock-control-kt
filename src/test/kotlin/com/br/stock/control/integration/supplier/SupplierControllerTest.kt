package com.br.stock.control.integration.supplier

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.supplier.CreateSupplierDTO
import com.br.stock.control.model.dto.supplier.UpdateSupplierDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Supplier
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
import java.util.UUID
import kotlin.String
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class SupplierControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var facadeRepository: FacadeRepository

    @BeforeEach fun setup() {
        facadeRepository.userRepository.deleteAll()
        facadeRepository.categoryRepository.deleteAll()
        facadeRepository.supplierRepository.deleteAll()
    }

    private val urlSupplier = "/v1/supplier"

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

    @Test
    fun `should create a supplier`() {
        val responseToken: ResponseToken = createUserAndLog()
        val user: UserDTO = getUser(responseToken.token)

        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>

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
        assertThat(response.body.cnpj).isEqualTo(dto.cnpj).withFailMessage("cnpj is different")
        assertThat(response.body.nameEnterprise).isEqualTo(dto.nameEnterprise).withFailMessage("nameEnterprise is different")
        assertThat(response.body.notes).isEqualTo(dto.notes).withFailMessage("notes is different")
        assertThat(response.body.type).isEqualTo(dto.type).withFailMessage("type is different")
        assertThat(response.body.rating).isEqualTo(dto.rating).withFailMessage("rating is different")
        assertThat(response.body.createdBy).isEqualTo(dto.createdBy).withFailMessage("createdBy is different")
    }

    @Test
    fun `should get supplier`() {
        val responseToken: ResponseToken = createUserAndLog()
        val user: UserDTO = getUser(responseToken.token)
        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier = createSupplier(responseToken, user, categories)

        val mvcResult = mockMvc.perform(
            get(this.urlSupplier+"/me")
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Supplier> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Supplier>>() {}
        )

        assertThat(response.message).isEqualTo("Supplier found").withFailMessage("Message is different")
        assertThat(response.body.userId).isEqualTo(user.id).withFailMessage("User Id is different")
        assertThat(response.body.cnpj).isEqualTo(supplier.cnpj).withFailMessage("cnpj is different")
        assertThat(response.body.nameEnterprise).isEqualTo(supplier.nameEnterprise).withFailMessage("nameEnterprise is different")
        assertThat(response.body.notes).isEqualTo(supplier.notes).withFailMessage("notes is different")
        assertThat(response.body.type).isEqualTo(supplier.type).withFailMessage("type is different")
        assertThat(response.body.rating).isEqualTo(supplier.rating).withFailMessage("rating is different")
        assertThat(response.body.createdBy).isEqualTo(supplier.createdBy).withFailMessage("createdBy is different")
    }

    @Test
    fun `should delete supplier`() {
        val responseToken: ResponseToken = createUserAndLog()
        val user: UserDTO = getUser(responseToken.token)
        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        createSupplier(responseToken, user, categories)

        val mvcResult = mockMvc.perform(
            delete(this.urlSupplier)
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Unit> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message).isEqualTo("Supplier deleted").withFailMessage("Message is different")
    }

    @Test
    fun `should update supplier`() {
        val responseToken: ResponseToken = createUserAndLog()
        val user: UserDTO = getUser(responseToken.token)
        val categories = (1..5).map { createCategory(responseToken.token).id } as MutableList<String>
        val supplier = createSupplier(responseToken, user, categories)

        val dto = UpdateSupplierDTO(
            cnpj = Random.nextLong(10000000000).toString(),
            nameEnterprise = "Lorem enterprise inc",
            notes = supplier.notes?: "no notes",
            type = SupplierTypeEnum.GOVERNMENT,
            rating = Random.nextInt(1,11),
            categoriesId = categories
        )
        
        val mvcResult = mockMvc.perform(
            put(this.urlSupplier)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Supplier> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Supplier>>() {}
        )

        assertThat(response.message).isEqualTo("Supplier updated").withFailMessage("Message is different")
        assertThat(response.body.userId).isEqualTo(user.id).withFailMessage("User Id is different")
        assertThat(response.body.cnpj).isEqualTo(dto.cnpj).withFailMessage("cnpj is different")
        assertThat(response.body.nameEnterprise).isEqualTo(dto.nameEnterprise).withFailMessage("nameEnterprise is different")
        assertThat(response.body.notes).isEqualTo(dto.notes).withFailMessage("notes is different")
        assertThat(response.body.type).isEqualTo(dto.type).withFailMessage("type is different")
        assertThat(response.body.rating).isEqualTo(dto.rating).withFailMessage("rating is different")
    }

    @Test
    fun `should change status isPreferred`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseToken2: ResponseToken = createUserAndLog()
        val user: UserDTO = getUser(responseToken.token)
        val categories = (1..5).map { createCategory(responseToken.token).id }.toMutableList()
        val supplier = createSupplier(responseToken, user, categories)

        val mvcResult = mockMvc.perform(
            put("${this.urlSupplier}/${supplier.userId}/status/preferred")
                .header("Authorization", "Bearer ${responseToken2.token}")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Supplier> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Supplier>>() {}
        )

        assertThat(response.message).isEqualTo("Status preferred changed").withFailMessage("Message is different")
        assertThat(response.body.isPreferred).isTrue.withFailMessage("Preferred is false")
    }

}