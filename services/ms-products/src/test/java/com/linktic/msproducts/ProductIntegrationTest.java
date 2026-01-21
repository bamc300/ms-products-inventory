package com.linktic.msproducts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.msproducts.application.dto.CreateProductRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private String apiKey;

        @BeforeEach
        void setUp() {
                apiKey = "inventory-microservices-api-key-12345";
        }

        @Test
        void getAllProducts_ShouldReturn200() throws Exception {
                CreateProductRequestDto request = CreateProductRequestDto.builder()
                                .data(CreateProductRequestDto.ProductData.builder().type("products")
                                                .attributes(CreateProductRequestDto.ProductAttributes
                                                                .builder().nombre("List Product")
                                                                .precio(new BigDecimal("10.00"))
                                                                .descripcion("Desc").build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/products").header("X-API-Key", apiKey)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/v1/products").header("X-API-Key", apiKey))
                                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void createProduct_ShouldReturn201() throws Exception {
                CreateProductRequestDto request = CreateProductRequestDto.builder()
                                .data(CreateProductRequestDto.ProductData.builder().type("products")
                                                .attributes(CreateProductRequestDto.ProductAttributes
                                                                .builder()
                                                                .nombre("Integration Product")
                                                                .precio(new BigDecimal("99.99"))
                                                                .descripcion("Desc").build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/products").header("X-API-Key", apiKey)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.type").value("products"))
                                .andExpect(jsonPath("$.data.attributes.nombre")
                                                .value("Integration Product"));
        }

        @Test
        void getProduct_ShouldReturn200_WhenExists() throws Exception {
                CreateProductRequestDto request = CreateProductRequestDto.builder()
                                .data(CreateProductRequestDto.ProductData.builder().type("products")
                                                .attributes(CreateProductRequestDto.ProductAttributes
                                                                .builder().nombre("Get Product")
                                                                .precio(new BigDecimal("10.00"))
                                                                .descripcion("Desc").build())
                                                .build())
                                .build();

                MvcResult created = mockMvc
                                .perform(post("/api/v1/products").header("X-API-Key", apiKey)
                                                .contentType("application/vnd.api+json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated()).andReturn();

                JsonNode createdJson =
                                objectMapper.readTree(created.getResponse().getContentAsString());
                String id = createdJson.path("data").path("id").asText();

                mockMvc.perform(get("/api/v1/products/{id}", id).header("X-API-Key", apiKey))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value(id))
                                .andExpect(jsonPath("$.data.attributes.nombre")
                                                .value("Get Product"));
        }

        @Test
        void getProduct_ShouldReturn404_WhenNotFound() throws Exception {
                mockMvc.perform(get("/api/v1/products/{id}", "99999999-9999-9999-9999-999999999999")
                                .header("X-API-Key", apiKey)).andExpect(status().isNotFound());
        }
}
