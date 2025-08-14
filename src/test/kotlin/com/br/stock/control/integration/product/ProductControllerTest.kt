package com.br.stock.control.integration.product

import com.br.stock.control.model.dto.category.CreateCategoryDTO
import com.br.stock.control.model.dto.product.CreateProductDTO
import com.br.stock.control.model.dto.user.LoginUserDTO
import com.br.stock.control.model.dto.user.RegisterUserDTO
import com.br.stock.control.model.dto.user.UserDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.responses.ResponseBody
import com.br.stock.control.util.responses.ResponseToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertNotNull
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var facadeRepository: FacadeRepository

    private val url: String = "/v1/product/"

    fun createUserAndLog(): ResponseToken {
        val num = Random.nextInt(100)
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

        val response: ResponseToken = objectMapper.convertValue(
            readTree,
            object : TypeReference<ResponseToken>() {}
        )

        assertThat(response.token).isNotBlank
        assertThat(response.refreshToken).isNotBlank

        return response
    }

    fun createCategory(token: String): ResponseBody<Category> {
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

        val node: JsonNode = objectMapper.readTree(result1.response.contentAsString)

        val body = objectMapper.convertValue(
            node,
            object : TypeReference<ResponseBody<Category>>() {}
        )

        assertThat(body).isNotNull
        assertThat(body.body?.id).isNotNull
        assertThat(body.body?.name).isEqualTo(dtoCategory.name)
        assertThat(body.body?.description).isEqualTo(dtoCategory.description)

        return body
    }

    @Test
    fun `should create new product`() {
        val responseTokens: ResponseToken = createUserAndLog()
        val responseCategory = createCategory(responseTokens.token)
        val uuid = "${UUID.randomUUID()}"

        val dto = CreateProductDTO(
            name = "name product $uuid", description= "description $uuid", sku = uuid,
            barcode = "${Random.nextLong(100000000)}", unitOfMeasure = UnitOfMeasureEnum.UNIT,
            price = BigDecimal.valueOf(Random.nextDouble(99999.99)),
            cost = BigDecimal.valueOf(Random.nextDouble(99999.99)),
            imageUrl= "", minStockLevel = Random.nextInt(100), maxStockLevel = Random.nextInt(100000) + 100
        )

        val mvcResult: MvcResult = mockMvc.perform(
            post("$url/${responseCategory.body?.id}")
                .header("Authorization", "Bearer ${responseTokens.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated).andReturn()

        val response: ResponseBody<Product> = objectMapper.convertValue(
            mvcResult.response,
            object : TypeReference<ResponseBody<Product>>() {}
        )

        assertThat(response.message).isEqualTo("Product created")
        assertThat(response.body?.id).isNotNull
        assertThat(response.body?.name).isEqualTo(dto.name)
        assertThat(response.body?.description).isEqualTo(dto.description)
        assertThat(response.body?.sku).isEqualTo(dto.sku)
        assertThat(response.body?.price).isEqualTo(dto.price)
        assertThat(response.body?.cost).isEqualTo(dto.cost)
        assertThat(response.body?.minStockLevel).isEqualTo(dto.minStockLevel)
        assertThat(response.body?.maxStockLevel).isEqualTo(dto.maxStockLevel)
        assertThat(response.body?.categoryId)
            .isEqualTo(responseCategory.body?.id).withFailMessage("Categories ids are different")

    }


}