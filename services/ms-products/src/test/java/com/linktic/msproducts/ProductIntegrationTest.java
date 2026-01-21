package com.linktic.msproducts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.msproducts.application.dto.CreateProductRequestDto;
import com.linktic.msproducts.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

        @Autowired
        private JwtService jwtService;

        private String token;

        @BeforeEach
        void setUp() {
                UserDetails user = User.builder().username("admin").password("password")
                                .roles("ADMIN").build();
                token = "Bearer " + jwtService.generateToken(user);
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

                mockMvc.perform(post("/api/v1/products").header("Authorization", token)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.type").value("product"))
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
                                .perform(post("/api/v1/products").header("Authorization", token)
                                                .contentType("application/vnd.api+json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated()).andReturn();

                JsonNode createdJson =
                                objectMapper.readTree(created.getResponse().getContentAsString());
                String id = createdJson.path("data").path("id").asText();

                mockMvc.perform(get("/api/v1/products/{id}", id).header("Authorization", token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value(id))
                                .andExpect(jsonPath("$.data.attributes.nombre")
                                                .value("Get Product"));
        }

        @Test
        void getProduct_ShouldReturn404_WhenNotFound() throws Exception {
                mockMvc.perform(get("/api/v1/products/{id}", "99999999-9999-9999-9999-999999999999")
                                .header("Authorization", token)).andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errors[0].status").value("404"));
        }

        @Test
        void createProduct_ShouldReturn400_WhenValidationFails() throws Exception {
                CreateProductRequestDto request = CreateProductRequestDto.builder()
                                .data(CreateProductRequestDto.ProductData.builder().type("products")
                                                .attributes(CreateProductRequestDto.ProductAttributes
                                                                .builder().nombre("").precio(null)
                                                                .descripcion("Desc").build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/products").header("Authorization", token)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors[0].title").value("Validation Error"));
        }

        @Test
        void createProduct_ShouldReturn403_WhenMissingToken() throws Exception {
                CreateProductRequestDto request = CreateProductRequestDto.builder()
                                .data(CreateProductRequestDto.ProductData.builder().type("products")
                                                .attributes(CreateProductRequestDto.ProductAttributes
                                                                .builder().nombre("No Key")
                                                                .precio(new BigDecimal("5.00"))
                                                                .descripcion("Desc").build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/products").contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }
}
