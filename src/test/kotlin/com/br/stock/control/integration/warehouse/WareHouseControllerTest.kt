package com.br.stock.control.integration.warehouse

import com.br.stock.control.model.dto.address.CreateAddressDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.dto.warehouse.CreateWareDTO
import com.br.stock.control.model.dto.warehouse.UpdateWareDTO
import com.br.stock.control.model.entity.Address
import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.TypeAddressEnum
import com.br.stock.control.model.enum.WareHouseEnum
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.responses.ResponseBody
import com.br.stock.control.util.responses.ResponseToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
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
import kotlin.random.nextLong

@SpringBootTest
@AutoConfigureMockMvc
class WareHouseControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/ware"

    @BeforeEach
    fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.wareHouseRepository.deleteAll()
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

    fun createWareHouse(responseToken: ResponseToken): ResponseBody<Warehouse?> {
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
            post(this.url)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated).andReturn()

        val readTree: JsonNode = objectMapper.readTree(mvcResult.response.contentAsString)

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Warehouse created")
        assertThat(response.body!!.id).isNotNull

        return response
    }

    @Test
    fun `should create a warehouse`() {
        val responseToken: ResponseToken = createUserAndLog()

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
            post(this.url)
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated).andReturn()

        val readTree: JsonNode = objectMapper.readTree(mvcResult.response.contentAsString)

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Warehouse created")
        assertThat(response.body!!.id).isNotNull
        assertThat(response.body!!.name).isEqualTo(dto.name)
        assertThat(response.body!!.description).isEqualTo(dto.description)
        assertThat(response.body!!.responsibleUserId).isEqualTo(dto.responsibleUserId)
        assertThat(response.body!!.amount).isEqualTo(dto.amount)
        assertThat(response.body!!.capacityCubicMeters).isEqualTo(dto.capacityCubicMeters)
        assertThat(response.body!!.type).isEqualTo(dto.type)
    }

    @Test
    fun `should get warehouse`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseWareHouse: ResponseBody<Warehouse?> = createWareHouse(responseToken)

        val mvcResult = mockMvc.perform(
            get("/v1/ware/${responseWareHouse.body?.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andReturn()

        val readTree: JsonNode = objectMapper.readTree(mvcResult.response.contentAsString)

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Warehouse found")
        assertThat(response.body!!.id).isEqualTo(responseWareHouse.body?.id)
        assertThat(response.body!!.name).isEqualTo(responseWareHouse.body?.name)
        assertThat(response.body!!.description).isEqualTo(responseWareHouse.body?.description)
        assertThat(response.body!!.responsibleUserId).isEqualTo(responseWareHouse.body?.responsibleUserId)
        assertThat(response.body!!.amount).isEqualTo(responseWareHouse.body?.amount)
        assertThat(response.body!!.capacityCubicMeters).isEqualTo(responseWareHouse.body?.capacityCubicMeters)
        assertThat(response.body!!.type).isEqualTo(responseWareHouse.body?.type)
        
    }

    @Test
    fun `should delete warehouse`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseWareHouse: ResponseBody<Warehouse?> = createWareHouse(responseToken)

        val mvcResult = mockMvc.perform(
            delete("$url/${responseWareHouse.body?.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNull()
        assertThat(response.message).isEqualTo("Warehouse deleted").withFailMessage("Msg is different")
    }

    @Test
    fun `should delete many warehouse`() {
        val responseToken: ResponseToken = createUserAndLog()

        val first = createWareHouse(responseToken)
        var ids: String = "${first.body?.id}"

        repeat(9) {
            val another = createWareHouse(responseToken)
            ids += ",${another.body?.id}"
        }

        val mvcResult = mockMvc.perform(
            delete("$url/$ids/many")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk)
            .andReturn()

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNull()
        assertThat(response.message)
            .isEqualTo("Warehouse many deleted")
            .withFailMessage("Msg is different")
    }

    @Test
    fun `should update warehouse`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseWareHouse: ResponseBody<Warehouse?> = createWareHouse(responseToken)

        val mvcResult = mockMvc.perform(
            get("/v1/ware/${responseWareHouse.body?.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )
        
        val dto = UpdateWareDTO(
            name = "${response.body?.name} updated",
            description = "${response.body?.description} updated ",
            responsibleUserId = "${response.body?.responsibleUserId}",
            amount= Random.nextLong(100000) ,
            capacityCubicMeters = response.body?.capacityCubicMeters?.plus(Random.nextDouble(99.99)) ?: Random.nextDouble(9999999.99),
            type = WareHouseEnum.DANGER
        )

        val mvcResultPut: MvcResult = mockMvc.perform(
            put(this.url+"/${response.body?.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk).andReturn()

        val responsePut: ResponseBody<Warehouse?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResultPut.response.contentAsString),
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(responsePut.body).isNotNull
        assertThat(responsePut.message).isEqualTo("Warehouse updated")
        assertThat(responsePut.body!!.id).isEqualTo(responseWareHouse.body?.id)
        assertThat(responsePut.body!!.name).isEqualTo(dto.name)
        assertThat(responsePut.body!!.description).isEqualTo(dto.description)
        assertThat(responsePut.body!!.responsibleUserId).isEqualTo(dto.responsibleUserId)
        assertThat(responsePut.body!!.amount).isEqualTo(dto.amount)
        assertThat(responsePut.body!!.capacityCubicMeters).isEqualTo(dto.capacityCubicMeters)
        assertThat(responsePut.body!!.type).isEqualTo(dto.type)

    }

    @Test
    fun `should change status active`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseWareHouse: ResponseBody<Warehouse?> = createWareHouse(responseToken)

        val mvcResult = mockMvc.perform(
            put("$url/${responseWareHouse.body?.id}/status/active")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNotNull.withFailMessage("Response came null")
        assertThat(response.body!!.id).isEqualTo(responseWareHouse.body?.id).withFailMessage("Id are different")
        assertThat(response.body!!.isActive).isFalse.withFailMessage("Status active is true")
    }

    @Test
    fun `should change status canToAdd`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseWareHouse: ResponseBody<Warehouse?> = createWareHouse(responseToken)

        val mvcResult = mockMvc.perform(
            put("$url/${responseWareHouse.body?.id}/status/add")
                .header("Authorization", "Bearer ${responseToken.token}")
        )
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Warehouse?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Warehouse?>>() {}
        )

        assertThat(response.body).isNotNull.withFailMessage("Response came null")
        assertThat(response.body!!.id).isEqualTo(responseWareHouse.body?.id).withFailMessage("Id are different")
        assertThat(response.body!!.canToAdd).isFalse.withFailMessage("Status can to add is true")
    }

    @Test
    fun `should create a address to ware house`() {
        val responseToken: ResponseToken = createUserAndLog()
        val responseWareHouse: ResponseBody<Warehouse?> = createWareHouse(responseToken)

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
            post(this.url + "/${responseWareHouse.body?.id}/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Address> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Address>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Address warehouse created")
        assertThat(response.body.id).isEqualTo(responseWareHouse.body?.id)
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
        assertThat(response.body.type).isEqualTo(TypeAddressEnum.WARE_HOUSE)
    }

}