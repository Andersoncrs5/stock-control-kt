package com.br.stock.control.integration.address

import com.br.stock.control.model.dto.address.CreateAddressDTO
import com.br.stock.control.model.dto.address.UpdateAddressDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.dto.warehouse.CreateWareDTO
import com.br.stock.control.model.entity.Address
import com.br.stock.control.model.entity.Warehouse
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

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/ware"
    private val urlAddress: String = "/v1/address"

    @BeforeEach fun setup() {
        this.facadeRepository.userRepository.deleteAll()
        this.facadeRepository.wareHouseRepository.deleteAll()
        this.facadeRepository.addressRepository.deleteAll()
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

    fun createWareHouse(responseToken: ResponseToken): Warehouse {
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

        val response: ResponseBody<Warehouse> = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseBody<Warehouse>>() {}
        )

        assertThat(response.body).isNotNull
        assertThat(response.message).isEqualTo("Warehouse created")
        assertThat(response.body.id).isNotNull

        return response.body
    }

    fun createAddress(responseToken: ResponseToken, wareHouse: Warehouse): Address {
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
            post(this.url + "/${wareHouse.id}/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Address> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Address>>() {}
        )

        return response.body
    }

    @Test
    fun `should get address`() {
        val responseToken: ResponseToken = createUserAndLog()
        val createWareHouse: Warehouse = createWareHouse(responseToken)
        val address: Address = createAddress(responseToken, createWareHouse)

        val mvcResult = mockMvc.perform(
            get(this.urlAddress + "/${address.id}").header("Authorization", "Bearer ${responseToken.token}"))
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Address?> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Address?>>() {}
        )

        assertThat(response.body).isNotNull.withFailMessage("Body came null")
        assertThat(response.message).isEqualTo("Address found").withFailMessage("Msg are different")
        assertThat(response.body?.id).isEqualTo(address.id).withFailMessage("ids are different")
        assertThat(response.body?.street).isEqualTo(address.street).withFailMessage("Streets are different")
        assertThat(response.body?.number).isEqualTo(address.number).withFailMessage("Numbers are different")
        assertThat(response.body?.complement).isEqualTo(address.complement).withFailMessage("Complements are different")
        assertThat(response.body?.neighborhood).isEqualTo(address.neighborhood).withFailMessage("Neighborhoods are different")
        assertThat(response.body?.city).isEqualTo(address.city).withFailMessage("Cities are different")
        assertThat(response.body?.state).isEqualTo(address.state).withFailMessage("States are different")
        assertThat(response.body?.zipCode).isEqualTo(address.zipCode).withFailMessage("ZipCodes are different")
        assertThat(response.body?.country).isEqualTo(address.country).withFailMessage("Contries are different")
        assertThat(response.body?.referencePoint).isEqualTo(address.referencePoint).withFailMessage("Reference Points are different")
        assertThat(response.body?.latitude).isEqualTo(address.latitude).withFailMessage("Latitudes are different")
        assertThat(response.body?.longitude).isEqualTo(address.longitude).withFailMessage("Longitudes are different")
        assertThat(response.body?.isActive).isEqualTo(address.isActive).withFailMessage("isActive are different")
        assertThat(response.body?.type).isEqualTo(address.type).withFailMessage("Types are different")
    }

    @Test
    fun `should delete address`() {
        val responseToken: ResponseToken = createUserAndLog()
        val createWareHouse: Warehouse = createWareHouse(responseToken)
        val address: Address = createAddress(responseToken, createWareHouse)

        val mvcResult = mockMvc.perform(
            delete(this.urlAddress + "/${address.id}").header("Authorization", "Bearer ${responseToken.token}"))
            .andExpect(status().isNoContent).andReturn()

        val response: ResponseBody<Unit> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Unit>>() {}
        )

        assertThat(response.body).isInstanceOf(Unit::class.java)
        assertThat(response.message).isEqualTo("Address deleted").withFailMessage("Msg are different")
    }

    @Test
    fun `should update address`() {
        val responseToken: ResponseToken = createUserAndLog()
        val createWareHouse: Warehouse = createWareHouse(responseToken)
        val address: Address = createAddress(responseToken, createWareHouse)

        val num: Long = Random.nextLong(100000)
        val dto = UpdateAddressDTO(
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
            put(this.urlAddress + "/${address.id}")
                .header("Authorization", "Bearer ${responseToken.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Address> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Address>>() {}
        )

        assertThat(response.message).isEqualTo("Address updated")
        assertThat(response.body.id).isEqualTo(address.id).withFailMessage("Ids are different")
        assertThat(response.body.street).isEqualTo(dto.street).withFailMessage("Streets are different")
        assertThat(response.body.number).isEqualTo(dto.number).withFailMessage("Numbers are different")
        assertThat(response.body.complement).isEqualTo(dto.complement).withFailMessage("complements are different")
        assertThat(response.body.neighborhood).isEqualTo(dto.neighborhood).withFailMessage("neighborhood are different")
        assertThat(response.body.city).isEqualTo(dto.city).withFailMessage("Cities are different")
        assertThat(response.body.state).isEqualTo(dto.state).withFailMessage("states are different")
        assertThat(response.body.zipCode).isEqualTo(dto.zipCode).withFailMessage("zipCodes are different")
        assertThat(response.body.country).isEqualTo(dto.country).withFailMessage("countries are different")
        assertThat(response.body.referencePoint).isEqualTo(dto.referencePoint).withFailMessage("referencePoint are different")
        assertThat(response.body.latitude).isEqualTo(dto.latitude).withFailMessage("latitude are different")
        assertThat(response.body.longitude).isEqualTo(dto.longitude).withFailMessage("longitude are different")
        assertThat(response.body.isActive).isTrue.withFailMessage("isActive are different")
    }

    @Test
    fun `should change status active`() {
        val responseToken: ResponseToken = createUserAndLog()
        val createWareHouse: Warehouse = createWareHouse(responseToken)
        val address: Address = createAddress(responseToken, createWareHouse)

        val mvcResult = mockMvc.perform(
            put(this.urlAddress + "/${address.id}/status/active")
                .header("Authorization", "Bearer ${responseToken.token}"))
            .andExpect(status().isOk).andReturn()

        val response: ResponseBody<Address> = objectMapper.convertValue(
            objectMapper.readTree(mvcResult.response.contentAsString),
            object : TypeReference<ResponseBody<Address>>() {}
        )

        assertThat(response.message).isEqualTo("Address status changed")
        assertThat(response.body.id).isEqualTo(address.id).withFailMessage("Ids are different")
        assertThat(response.body.street).isEqualTo(address.street).withFailMessage("Streets are different")
        assertThat(response.body.number).isEqualTo(address.number).withFailMessage("Numbers are different")
        assertThat(response.body.complement).isEqualTo(address.complement).withFailMessage("complements are different")
        assertThat(response.body.neighborhood).isEqualTo(address.neighborhood).withFailMessage("neighborhood are different")
        assertThat(response.body.city).isEqualTo(address.city).withFailMessage("Cities are different")
        assertThat(response.body.state).isEqualTo(address.state).withFailMessage("states are different")
        assertThat(response.body.zipCode).isEqualTo(address.zipCode).withFailMessage("zipCodes are different")
        assertThat(response.body.country).isEqualTo(address.country).withFailMessage("countries are different")
        assertThat(response.body.referencePoint).isEqualTo(address.referencePoint).withFailMessage("referencePoint are different")
        assertThat(response.body.latitude).isEqualTo(address.latitude).withFailMessage("latitude are different")
        assertThat(response.body.longitude).isEqualTo(address.longitude).withFailMessage("longitude are different")
        assertThat(response.body.isActive).isFalse.withFailMessage("isActive is true")
    }

}