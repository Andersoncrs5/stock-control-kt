package com.br.stock.control.integration.user

import com.br.stock.control.model.dto.address.CreateAddressDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UpdateUserDTO
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Address
import com.br.stock.control.model.enum.TypeAddressEnum
import com.br.stock.control.util.responses.ResponseBody
import com.fasterxml.jackson.core.type.TypeReference
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat

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

    fun getUser(token: String): UserDTO {
        val mvcResult = mockMvc.perform(
            get(this.url + "/me")
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

    @Test
    fun `should updated user`() {
        val node: JsonNode = this.createUserAndLog()

        val token = node.get("body")?.get("token")?.asText()
        assertNotNull(token, "Token came null")

        val dto = UpdateUserDTO("user update", "12345678", "user full name updated")

        mockMvc.perform(put(this.url)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.body.name").value(dto.name))
            .andExpect(jsonPath("$.body.fullName").value(dto.fullName))
    }

    @Test
    fun `should create a address to user`() {
        val node: JsonNode = this.createUserAndLog()
        val token = node.get("body")?.get("token")?.asText()
        assertNotNull(token)

        val userDTO: UserDTO = getUser(token)
        val num: Long = Random.nextLong(100000)
        val dto = CreateAddressDTO(
            street = "street $num",
            number = "$num",
            complement = "complement $num",
            neighborhood = "neighborhood $num",
            city = "city $num",
            state = "state $num",
            zipCode = "$num",
            country = "US",
            referencePoint = "$num",
            latitude= Random.nextDouble(999999.999),
            longitude = Random.nextDouble(999999.999),
            isActive = true
        )

        val mvcResult = mockMvc.perform(
            post(this.url + "/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer $token"))
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Address> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Address>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Address user created")
        assertThat(response.body.id).isEqualTo(userDTO.id)
        assertThat(response.body.street).isEqualTo(dto.street)
        assertThat(response.body.number).isEqualTo(dto.number)
        assertThat(response.body.complement).isEqualTo(dto.complement)
        assertThat(response.body.neighborhood).isEqualTo(dto.neighborhood)
        assertThat(response.body.city).isEqualTo(dto.city)
        assertThat(response.body.state).isEqualTo(dto.state)
        assertThat(response.body.zipCode).isEqualTo(dto.zipCode)
        assertThat(response.body.country).isEqualTo(dto.country)
        assertThat(response.body.referencePoint).isEqualTo(dto.referencePoint)
        assertThat(response.body.latitude).isEqualTo(dto.latitude)
        assertThat(response.body.longitude).isEqualTo(dto.longitude)
        assertThat(response.body.isActive).isEqualTo(dto.isActive)
        assertThat(response.body.type).isEqualTo(TypeAddressEnum.USER)
    }

}