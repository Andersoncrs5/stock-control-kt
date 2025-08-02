package com.br.stock.control.integration.user

import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.mappers.user.LoginUserDTO
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
import java.util.*
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/user"

    @BeforeEach
    fun setup() {
        facadeRepository.userRepository.deleteAll()
    }

    fun createUser() {
        val num = Random.nextInt(100)
        val dto: RegisterUserDTO = RegisterUserDTO("user${num}",email = "user${num}@gmail.com".trim().lowercase(),UUID.randomUUID().toString(),"user $num")

        mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated)
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

    @Test
    fun `should get user`() {
        val node: JsonNode = this.createUserAndLog()

        val token = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        mockMvc.perform(get(this.url+"/me")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User founded"))
    }

    @Test
    fun `should delete user`() {
        val node: JsonNode = this.createUserAndLog()

        val token = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        mockMvc.perform(delete(this.url+"/delete")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User deleted"))
    }



}