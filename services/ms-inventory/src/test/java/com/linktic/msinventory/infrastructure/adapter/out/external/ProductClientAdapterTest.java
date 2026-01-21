package com.linktic.msinventory.infrastructure.adapter.out.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.msinventory.domain.exception.ExternalServiceException;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ProductClientAdapterTest {

    private ProductClientAdapter productClientAdapter;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        productClientAdapter = new ProductClientAdapter(restClient, "test-api-key");
        objectMapper = new ObjectMapper();
    }

    @Test
    void getProductById_Success_ShouldReturnProduct() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        ProductClientAdapter.ProductResponseWrapper wrapper =
                new ProductClientAdapter.ProductResponseWrapper();
        ProductClientAdapter.ProductData data = new ProductClientAdapter.ProductData();
        data.setId(id.toString());
        ProductClientAdapter.ProductAttributes attributes =
                new ProductClientAdapter.ProductAttributes();
        attributes.setNombre("Test Product");
        attributes.setPrecio(BigDecimal.valueOf(100.0));
        data.setAttributes(attributes);
        wrapper.setData(data);

        mockServer.expect(requestTo("/api/v1/products/" + id))
                .andExpect(header("X-API-Key", "test-api-key")).andRespond(withSuccess(
                        objectMapper.writeValueAsString(wrapper), MediaType.APPLICATION_JSON));

        Optional<ProductClientPort.ProductDto> result = productClientAdapter.getProductById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Test Product", result.get().getNombre());
        assertEquals(BigDecimal.valueOf(100.0), result.get().getPrecio());
    }

    @Test
    void getProductById_NotFound_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();

        mockServer.expect(requestTo("/api/v1/products/" + id)).andRespond(withResourceNotFound());

        Optional<ProductClientPort.ProductDto> result = productClientAdapter.getProductById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void getProductById_ServerError_ShouldRetryAndThrowException() {
        UUID id = UUID.randomUUID();

        // Expect 3 calls (initial + 2 retries)
        mockServer.expect(requestTo("/api/v1/products/" + id)).andRespond(withServerError());
        mockServer.expect(requestTo("/api/v1/products/" + id)).andRespond(withServerError());
        mockServer.expect(requestTo("/api/v1/products/" + id)).andRespond(withServerError());

        assertThrows(ExternalServiceException.class, () -> productClientAdapter.getProductById(id));
    }

    @Test
    void getProductById_GenericException_ShouldThrowExternalServiceException() {
        UUID id = UUID.randomUUID();

        // Mock a runtime exception that is NOT one of the retriable ones
        // But MockRestServiceServer usually throws RestClientException which is caught.
        // Let's force a strange exception if possible, or just a malformed response that causes
        // JSON parsing error.

        mockServer.expect(requestTo("/api/v1/products/" + id))
                .andRespond(withSuccess("invalid json", MediaType.APPLICATION_JSON));

        assertThrows(ExternalServiceException.class, () -> productClientAdapter.getProductById(id));
    }
}
