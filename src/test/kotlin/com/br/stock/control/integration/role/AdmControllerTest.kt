package com.br.stock.control.integration.role

import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Role
import com.br.stock.control.model.entity.User
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
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class AdmControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var facadeRepository: FacadeRepository
    @Autowired private lateinit var facadesServices: FacadeServices

    private val urlUser: String = "/v1/user"
    private val urlAdm: String = "/v1/adm"

    @BeforeEach fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.roleRepository.deleteAll()
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

    fun createAndlogSuperAdm(): ResponseToken {
        val roleSuperAdmin = Role(
            id = UUID.randomUUID().toString(),
            name = "ROLE_SUPER_ADMIN",
            description = "Super administrator role",
            createdAt = LocalDate.now()
        )
        val roleAdmin = Role(
            id = UUID.randomUUID().toString(),
            name = "ROLE_ADMIN",
            description = "Admin role",
            createdAt = LocalDate.now()
        )

        this.facadeRepository.roleRepository.saveAll(listOf(roleAdmin, roleSuperAdmin))

        val email = "admin@gmail.com"

        val passwordPlain = Random.nextLong(10000000).toString()
        val passwordEncoded = facadesServices.cryptoService.encoderPassword(passwordPlain)

        val user = User(
            id = UUID.randomUUID().toString(),
            name = "SuperAdmin",
            email = email,
            passwordHash = passwordEncoded,
            fullName = "super admin",
            accountNonExpired = false,
            credentialsNonExpired = false,
            accountNonLocked = false,
            roles = setOf(roleSuperAdmin),
            contact = listOf(),
            lastLoginAt = null,
            refreshToken = null,
            version = 0,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now()
        )

        facadeRepository.userRepository.save(user)

        val dtoLog = LoginUserDTO(user.name, passwordPlain)

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

    @Test fun `should set role in user`() {
        val superAdmTokens = createAndlogSuperAdm()
        val superUser = getUser(superAdmTokens)

        val responseToken = createUserAndLog()
        val user = getUser(responseToken)

        val mvcResult = mockMvc.perform(
            post(this.urlAdm + "/role/${user.name}/set/admin")
                .header("Authorization", "Bearer ${superAdmTokens.token}")
        ).andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print()).andReturn()

        val response = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message)
            .isEqualTo("User ${user.name} now is a admin!")

    }

    @Test fun `should remove role in user`() {
        val superAdmTokens = createAndlogSuperAdm()
        val superUser = getUser(superAdmTokens)

        val responseToken = createUserAndLog()
        val user = getUser(responseToken)

        mockMvc.perform(
            post(this.urlAdm + "/role/${user.name}/set/admin")
                .header("Authorization", "Bearer ${superAdmTokens.token}")
        ).andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print()).andReturn()

        val mvcResult = mockMvc.perform(
            post(this.urlAdm + "/role/${user.name}/remove/admin")
                .header("Authorization", "Bearer ${superAdmTokens.token}")
        ).andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print()).andReturn()

        val response = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message)
            .isEqualTo("User ${user.name} is not a admin!")

    }

    @Test fun `should toggle user`() {
        val superAdmTokens = createAndlogSuperAdm()
        val superUser = getUser(superAdmTokens)

        val responseToken = createUserAndLog()
        val user = getUser(responseToken)

        val mvcResult = mockMvc.perform(
            put(this.urlAdm + "/toggle/${user.name}/lock")
                .header("Authorization", "Bearer ${superAdmTokens.token}")
        ).andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print()).andReturn()

        val response = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message)
            .isEqualTo("User ${user.name} is locked")

        val mvcResult2 = mockMvc.perform(
            put(this.urlAdm + "/toggle/${user.name}/lock")
                .header("Authorization", "Bearer ${superAdmTokens.token}")
        ).andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print()).andReturn()

        val response2 = objectMapper.convertValue(
            objectMapper.readTree(mvcResult2.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response2.message)
            .isEqualTo("User ${user.name} is unlocked")
    }

}