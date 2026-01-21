package com.linktic.msinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.msinventory.application.dto.PurchaseRequestDto;
import com.linktic.msinventory.application.dto.UpdateInventoryRequestDto;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PurchaseIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private InventoryRepositoryPort inventoryRepository;

        @MockBean
        private ProductClientPort productClient;

        private String apiKey;

        @BeforeEach
        void setUp() {
                apiKey = "inventory-microservices-api-key-12345";
                // Since we can't easily delete all from port, we rely on random UUIDs or assume
                // clean state/overwrite
        }

        @Test
        void purchase_ShouldReturn201_WhenSuccessful() throws Exception {
                UUID productId = UUID.randomUUID();
                inventoryRepository.save(
                                Inventory.builder().productId(productId).cantidad(10).build());

                when(productClient.getProductById(productId))
                                .thenReturn(Optional.of(ProductClientPort.ProductDto.builder()
                                                .id(productId).nombre("Test Product")
                                                .precio(new BigDecimal("100.0")).build()));

                PurchaseRequestDto request = PurchaseRequestDto.builder()
                                .data(PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                                .attributes(PurchaseRequestDto.PurchaseAttributes
                                                                .builder().productId(productId)
                                                                .quantity(2).build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/purchases").header("X-API-Key", apiKey)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.type").value("purchases"))
                                .andExpect(jsonPath("$.data.attributes.product-id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.cantidad-comprada").value(2));
        }
                                                
                                
                                                
        @Test
        void purchase_ShouldReturn409_WhenStockIsInsufficient() throws Exception {
                UUID productId = UUID.randomUUID();
                inventoryRepository
                                .save(Inventory.builder().productId(productId).cantidad(1).build());

                when(productClient.getProductById(productId))
                                .thenReturn(Optional.of(ProductClientPort.ProductDto.builder()
                                                .id(productId).nombre("Test Product")
                                                .precio(new BigDecimal("100.0")).build()));

                PurchaseRequestDto request = PurchaseRequestDto.builder()
                                .data(PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                                .attributes(PurchaseRequestDto.PurchaseAttributes
                                                                .builder().productId(productId)
                                                                .quantity(2).build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/purchases").header("X-API-Key", apiKey)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.errors[0].status").value("409"));
        }

        @Test
        void purchase_ShouldReturn404_WhenProductNotFound() throws Exception {
                UUID productId = UUID.randomUUID();
                inventoryRepository.save(
                                Inventory.builder().productId(productId).cantidad(10).build());

                when(productClient.getProductById(productId)).thenReturn(Optional.empty());

                PurchaseRequestDto request = PurchaseRequestDto.builder()
                                .data(PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                                .attributes(PurchaseRequestDto.PurchaseAttributes
                                                                .builder().productId(productId)
                                                                .quantity(2).build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/purchases").header("X-API-Key", apiKey)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errors[0].status").value("404"));
        }

        @Test
        void purchase_ShouldReturn400_WhenValidationFails() throws Exception {
                // Invalid request (missing productId or quantity < 1)
                PurchaseRequestDto request = PurchaseRequestDto.builder()
                                .data(PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                                .attributes(PurchaseRequestDto.PurchaseAttributes
                                                                .builder().productId(null) // Invalid
                                                                .quantity(0) // Invalid
                                                                .build())
                                                .build())
                                .build();

                mockMvc.perform(post("/api/v1/purchases").header("X-API-Key", apiKey)
                                .contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors[0].title").value("Validation Error"));
        }

        @Test
        void getInventory_ShouldReturn200() throws Exception {
                UUID productId = UUID.randomUUID();
                inventoryRepository.save(
                                Inventory.builder().productId(productId).cantidad(50).build());

                mockMvc.perform(get("/api/v1/inventory/{productId}", productId)
                                .header("X-API-Key", apiKey).accept("application/vnd.api+json"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.type").value("stocks"))
                                .andExpect(jsonPath("$.data.attributes.cantidad").value(50));
        }

        @Test
        void updateInventory_ShouldReturn200() throws Exception {
                UUID productId = UUID.randomUUID();
                inventoryRepository.save(
                                Inventory.builder().productId(productId).cantidad(10).build());

                UpdateInventoryRequestDto request = UpdateInventoryRequestDto.builder()
                                .data(UpdateInventoryRequestDto.InventoryUpdateData.builder()
                                                .type("stocks")
                                                .attributes(UpdateInventoryRequestDto.InventoryUpdateAttributes
                                                                .builder().cantidad(20).build())
                                                .build())
                                .build();

                mockMvc.perform(patch("/api/v1/inventory/{productId}", productId)
                                .header("X-API-Key", apiKey).contentType("application/vnd.api+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                                .andExpect(jsonPath("$.data.attributes.cantidad").value(30)); // 10
                                                                                              // +
                                                                                              // 20
                                                                                              // =
                                                                                              // 30
        }

        @Test
        void endpoints_ShouldReturn403_WhenMissingToken() throws Exception {
                mockMvc.perform(get("/api/v1/inventory/{productId}", UUID.randomUUID()))
                                .andExpect(status().isForbidden());
        }
}
