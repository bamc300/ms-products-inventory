package com.linktic.msinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.msinventory.application.dto.PurchaseRequestDto;
import com.linktic.msinventory.application.dto.UpdateInventoryRequestDto;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import com.linktic.msinventory.security.JwtService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ProductClientPort productClient;

    private String token;

    @BeforeEach
    void setUp() {
        UserDetails user =
                User.builder().username("admin").password("password").roles("ADMIN").build();
        token = "Bearer " + jwtService.generateToken(user);
    }

    @Test
    void purchase_ShouldReturn201_WhenStockExists() throws Exception {
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        inventoryRepository.save(Inventory.builder().productId(productId).cantidad(10).build());

        when(productClient.getProductById(productId))
                .thenReturn(Optional.of(ProductClientPort.ProductDto.builder().id(productId)
                        .nombre("Test Product").precio(new BigDecimal("50.0")).build()));

        PurchaseRequestDto request =
                PurchaseRequestDto
                        .builder().data(
                                PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                        .attributes(PurchaseRequestDto.PurchaseAttributes.builder()
                                                .productId(productId).quantity(2).build())
                                        .build())
                        .build();

        mockMvc.perform(post("/api/v1/purchases").header("Authorization", token)
                .contentType("application/vnd.api+json")
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("purchases"))
                .andExpect(jsonPath("$.data.attributes.product-id").value(productId.toString()));
    }

    @Test
    void purchase_ShouldReturn409_WhenStockIsInsufficient() throws Exception {
        UUID productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        inventoryRepository.save(Inventory.builder().productId(productId).cantidad(1).build());

        when(productClient.getProductById(productId)).thenReturn(
                Optional.of(ProductClientPort.ProductDto.builder().id(productId).build()));

        PurchaseRequestDto request =
                PurchaseRequestDto
                        .builder().data(
                                PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                        .attributes(PurchaseRequestDto.PurchaseAttributes.builder()
                                                .productId(productId).quantity(2).build())
                                        .build())
                        .build();

        mockMvc.perform(post("/api/v1/purchases").header("Authorization", token)
                .contentType("application/vnd.api+json")
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].status").value("409"));
    }

    @Test
    void purchase_ShouldReturn404_WhenProductNotFound() throws Exception {
        UUID productId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        inventoryRepository.save(Inventory.builder().productId(productId).cantidad(10).build());

        when(productClient.getProductById(productId)).thenReturn(Optional.empty());

        PurchaseRequestDto request =
                PurchaseRequestDto
                        .builder().data(
                                PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                        .attributes(PurchaseRequestDto.PurchaseAttributes.builder()
                                                .productId(productId).quantity(1).build())
                                        .build())
                        .build();

        mockMvc.perform(post("/api/v1/purchases").header("Authorization", token)
                .contentType("application/vnd.api+json")
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].status").value("404"));
    }

    @Test
    void purchase_ShouldReturn400_WhenValidationFails() throws Exception {
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        PurchaseRequestDto request =
                PurchaseRequestDto
                        .builder().data(
                                PurchaseRequestDto.PurchaseData.builder().type("purchases")
                                        .attributes(PurchaseRequestDto.PurchaseAttributes.builder()
                                                .productId(productId).quantity(0).build())
                                        .build())
                        .build();

        mockMvc.perform(post("/api/v1/purchases").header("Authorization", token)
                .contentType("application/vnd.api+json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].title").value("Validation Error"));
    }

    @Test
    void getInventory_ShouldReturn200() throws Exception {
        UUID productId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        inventoryRepository.save(Inventory.builder().productId(productId).cantidad(7).build());

        mockMvc.perform(
                get("/api/v1/inventory/{productId}", productId).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.cantidad").value(7));
    }

    @Test
    void updateInventory_ShouldReturn200() throws Exception {
        UUID productId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        inventoryRepository.save(Inventory.builder().productId(productId).cantidad(2).build());

        UpdateInventoryRequestDto request = UpdateInventoryRequestDto.builder()
                .data(UpdateInventoryRequestDto.InventoryUpdateData.builder().type("inventory")
                        .attributes(UpdateInventoryRequestDto.InventoryUpdateAttributes.builder()
                                .cantidad(5).build())
                        .build())
                .build();

        mockMvc.perform(patch("/api/v1/inventory/{productId}", productId)
                .header("Authorization", token).contentType("application/vnd.api+json")
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.cantidad").value(7));
    }

    @Test
    void endpoints_ShouldReturn403_WhenMissingToken() throws Exception {
        mockMvc.perform(
                get("/api/v1/inventory/{productId}", "11111111-1111-1111-1111-111111111111"))
                .andExpect(status().isForbidden());
    }
}
