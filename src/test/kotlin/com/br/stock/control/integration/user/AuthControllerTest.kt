package com.br.stock.control.integration.user

import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.mappers.user.LoginUserDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import java.util.UUID
import kotlin.random.Random

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/auth"

    @BeforeEach
    fun setup() {
        facadeRepository.userRepository.deleteAll()
    }

    @Test
    fun `should create new user`() {
        val num = Random.nextInt(100)
        val dto: RegisterUserDTO = RegisterUserDTO(
            "user${num}",
            email = "user${num}@gmail.com".trim().lowercase(),
            UUID.randomUUID().toString(),
            "user $num"
        )

        mockMvc.perform(post(this.url+"/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("User created"))
            .andExpect(jsonPath("$.body.id").isNotEmpty)
            .andExpect(jsonPath("$.body.id").isString)
            .andExpect(jsonPath("$.body.email").isNotEmpty)
            .andExpect(jsonPath("$.body.email").value(dto.email))
            .andExpect(jsonPath("$.body.name").isNotEmpty)
            .andExpect(jsonPath("$.body.name").value(dto.name))
            .andExpect(jsonPath("$.path").value("/v1/auth/register"))
            .andExpect(jsonPath("$.method").value("POST"))
    }

    @Test
    fun `should log user`() {
        val num = Random.nextInt(100)
        val dto: RegisterUserDTO = RegisterUserDTO(
            "user${num}",
            email = "user${num}@gmail.com".trim().lowercase(),
            UUID.randomUUID().toString(),
            "user $num"
        )

        mockMvc.perform(post(this.url+"/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)

        val dtoLog: LoginUserDTO = LoginUserDTO(dto.name, dto.passwordHash)

        mockMvc.perform(post(this.url+"/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dtoLog)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Logged with successfully!"))
            .andExpect(jsonPath("$.path").value(this.url+"/login"))
            .andExpect(jsonPath("$.method").value("POST"))
            .andExpect(jsonPath("$.body.token").isNotEmpty)
            .andExpect(jsonPath("$.body.token").isString)
            .andExpect(jsonPath("$.body.refreshToken").isNotEmpty)
            .andExpect(jsonPath("$.body.refreshToken").isString)
            .andExpect(jsonPath("$.body.expireAtToken").isNotEmpty)
            .andExpect(jsonPath("$.body.expireAtRefreshToken").isNotEmpty)
    }

}
