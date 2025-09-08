package com.br.stock.control.integration.contact

import com.br.stock.control.model.dto.contact.CreateContactDTO
import com.br.stock.control.model.dto.contact.UpdateContactDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Contact
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
class ContactControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var facadeRepository: FacadeRepository

    private val urlUser: String = "/v1/user"
    private val urlContact: String = "/v1/contact"

    @BeforeEach fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.contactRepository.deleteAll()
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

    fun createContact(responseToken: ResponseToken): Contact {
        val dto = CreateContactDTO(
            secondaryEmail = "user${Random.nextLong(1000000)}@gmail.com",
            phone = Random.nextLong(100000000).toString(),
            secondaryPhone = Random.nextLong(100000000).toString()
        )

        val mvcResult = mockMvc.perform(
            post(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message")
                .value("Contact created with successfully")).andReturn()

        val response: ResponseBody<Contact> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Contact>>() {}
        )

        return response.body
    }

    @Test fun `should create new contact`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)

        val dto = CreateContactDTO(
            secondaryEmail = "user${Random.nextLong(1000000)}@gmail.com",
            phone = Random.nextLong(100000000).toString(),
            secondaryPhone = Random.nextLong(100000000).toString()
        )

        val mvcResult = mockMvc.perform(
            post(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message")
                    .value("Contact created with successfully")).andReturn()

        val response: ResponseBody<Contact> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Contact>>() {}
        )

        assertThat(response.body.userId)
            .isEqualTo(userDTO.id)
            .withFailMessage("Id are different")

        assertThat(response.body.secondaryEmail)
            .isEqualTo(dto.secondaryEmail)
            .withFailMessage("secondaryEmail are different")

        assertThat(response.body.phone)
            .isEqualTo(dto.phone)
            .withFailMessage("phone are different")

        assertThat(response.body.secondaryPhone)
            .isEqualTo(dto.secondaryPhone)
            .withFailMessage("secondaryPhone are different")

    }

    @Test fun `should get contact`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val contact = createContact(responseToken)

        val mvcResult = mockMvc.perform(
            get(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.message")
                    .value("Contact found with successfully")
            ).andReturn()

        val response: ResponseBody<Contact> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Contact>>() {}
        )

        assertThat(response.body.userId)
            .isEqualTo(contact.userId)
            .withFailMessage("User id are different")

        assertThat(response.body.phone)
            .isEqualTo(contact.phone)
            .withFailMessage("phone are different")

        assertThat(response.body.secondaryEmail)
            .isEqualTo(contact.secondaryEmail)
            .withFailMessage("secondaryEmail are different")

        assertThat(response.body.secondaryPhone)
            .isEqualTo(contact.secondaryPhone)
            .withFailMessage("secondaryPhone are different")

        assertThat(response.body.createdAt)
            .isEqualTo(contact.createdAt)
            .withFailMessage("createdAt are different")
    }

    @Test fun `should get by userId contact`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val contact = createContact(responseToken)

        val mvcResult = mockMvc.perform(
            get(this.urlContact+"/${userDTO.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.message")
                    .value("Contact found with successfully")
            ).andReturn()

        val response: ResponseBody<Contact> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Contact>>() {}
        )

        assertThat(response.body.userId)
            .isEqualTo(contact.userId)
            .withFailMessage("User id are different")

        assertThat(response.body.phone)
            .isEqualTo(contact.phone)
            .withFailMessage("phone are different")

        assertThat(response.body.secondaryEmail)
            .isEqualTo(contact.secondaryEmail)
            .withFailMessage("secondaryEmail are different")

        assertThat(response.body.secondaryPhone)
            .isEqualTo(contact.secondaryPhone)
            .withFailMessage("secondaryPhone are different")

        assertThat(response.body.createdAt)
            .isEqualTo(contact.createdAt)
            .withFailMessage("createdAt are different")
    }

    @Test fun `should delete contact`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)
        val contact = createContact(responseToken)

        val mvcResult = mockMvc.perform(
            delete(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk).andReturn()

        val response = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.message)
            .isEqualTo("Contact deleted with successfully")
            .withFailMessage("Message are different")

        mockMvc.perform(
            get(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
        ).andExpect(status().isNotFound)

    }

    @Test fun `should update contact`() {
        val responseToken = createUserAndLog()
        val userDTO = getUser(responseToken)

        val contact = createContact(responseToken)

        val updateDto = UpdateContactDTO(
            secondaryEmail = "updated${Random.nextLong(1000000)}@gmail.com",
            phone = Random.nextLong(100000000).toString(),
            secondaryPhone = Random.nextLong(100000000).toString()
        )

        val mvcResult = mockMvc.perform(
            put(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Contact updated with successfully"))
            .andReturn()

        val response: ResponseBody<Contact> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Contact>>() {}
        )

        val updatedContact = response.body

        assertThat(updatedContact.userId)
            .isEqualTo(userDTO.id)
            .withFailMessage("UserId não deveria mudar")

        assertThat(updatedContact.secondaryEmail)
            .isEqualTo(updateDto.secondaryEmail)
            .withFailMessage("secondaryEmail não foi atualizado")

        assertThat(updatedContact.phone)
            .isEqualTo(updateDto.phone)
            .withFailMessage("phone não foi atualizado")

        assertThat(updatedContact.secondaryPhone)
            .isEqualTo(updateDto.secondaryPhone)
            .withFailMessage("secondaryPhone não foi atualizado")

        val getResult = mockMvc.perform(
            get(this.urlContact)
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andReturn()

        val getResponse: ResponseBody<Contact> = objectMapper.convertValue(
            objectMapper.readTree(getResult.response.contentAsString),
            object : TypeReference<ResponseBody<Contact>>() {}
        )

        assertThat(getResponse.body.phone)
            .isEqualTo(updateDto.phone)
            .withFailMessage("Banco não refletiu a atualização")
    }

}