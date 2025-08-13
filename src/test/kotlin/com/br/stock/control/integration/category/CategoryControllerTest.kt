package com.br.stock.control.integration.category

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.util.facades.FacadeRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
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
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/category"

    @BeforeEach
    fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.categoryRepository.deleteAll()
    }

    fun createUserAndLog(): JsonNode {
        val num = Random.nextInt(100)
        val dto: RegisterUserDTO = RegisterUserDTO("user${num}",email = "user${num}@gmail.com".trim().lowercase(),UUID.randomUUID().toString(),"user $num")

        mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated)

        val dtoLog = LoginUserDTO(dto.name, dto.passwordHash)

        val result: MvcResult = mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoLog))
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andReturn()

        return objectMapper.readTree(result.response.contentAsString)
    }

    fun createCategoryAndUser(): JsonNode {
        val num = Random.nextInt(100)
        val dto: RegisterUserDTO = RegisterUserDTO("user${num}",email = "user${num}@gmail.com".trim().lowercase(),UUID.randomUUID().toString(),"user $num")

        mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated)

        val dtoLog = LoginUserDTO(dto.name, dto.passwordHash)

        val result: MvcResult = mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoLog))
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andReturn()

        val node = objectMapper.readTree(result.response.contentAsString)

        val token: String? = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        val dtoCategory = CreateCategoryDTO(name = "category 1", description = "description 1")

        val result1 = mockMvc.perform(
            post(this.url)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoCategory))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andReturn()

        return objectMapper.readTree(result1.response.contentAsString)
    }

    fun createCategory(token: String): JsonNode {
        val dtoCategory = CreateCategoryDTO(name = "category 1" + Random.nextLong(10000), description = "description 1")

        val result1 = mockMvc.perform(
            post(this.url)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoCategory))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andReturn()

        return objectMapper.readTree(result1.response.contentAsString)
    }

    @Test
    fun `should create new category`() {
        val node: JsonNode = createUserAndLog()

        val token: String? = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        val dto = CreateCategoryDTO(name = "category 1", description = "description 1")

        mockMvc.perform(post(this.url)
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
    }

    @Test
    fun `should get category`() {
        val node: JsonNode = createUserAndLog()

        val token: String? = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        val nodeCategory = createCategory(token)

        val categoryId: String = nodeCategory.get("body").get("id").asText()
        assertNotNull(categoryId, "Category id is null")

        mockMvc.perform(get(this.url + "/$categoryId")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Category found!"))
            .andExpect(jsonPath("$.body.id").value(categoryId))
    }

    @Test
    fun `should get all category`() {
        val node: JsonNode = createUserAndLog()

        val token: String? = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        repeat(5) { createCategory(token) }

        val mvcResult = mockMvc.perform(
            get(this.url)
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk).andReturn()

        val response = mvcResult.response.contentAsString

        val nodeCategories: JsonNode = objectMapper.readTree(response)
        val bodyNode = nodeCategories.get("body")

        assertTrue(bodyNode.isArray, "Body is not an array")

        assertEquals(5, bodyNode.size(), "Expected 5 categories in response")
    }


}