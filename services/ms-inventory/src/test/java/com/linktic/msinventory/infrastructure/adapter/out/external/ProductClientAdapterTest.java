package com.linktic.msinventory.infrastructure.adapter.out.external;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductClientAdapterTest {

  private WireMockServer wireMockServer;
  private String apiKey = "test-api-key";

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
    wireMockServer.start();
    configureFor("localhost", wireMockServer.port());
  }

  @AfterEach
  void tearDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  @Test
  void getProductById_ShouldReturnProduct_When200() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    stubFor(get(urlEqualTo("/api/v1/products/" + productId))
        .withHeader("X-API-Key", equalTo(apiKey)).willReturn(okJson("{\"data\":{\"id\":\""
            + productId + "\",\"attributes\":{\"nombre\":\"P1\",\"precio\":50.0}}}")));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port(), apiKey);

    Optional<ProductClientPort.ProductDto> result = client.getProductById(productId);

    assertTrue(result.isPresent());
    assertEquals(productId, result.get().getId());
    assertEquals("P1", result.get().getNombre());
    assertEquals(new BigDecimal("50.0"), result.get().getPrecio());
  }

  @Test
  void getProductById_ShouldReturnEmpty_When404() {
    UUID productId = UUID.fromString("99999999-9999-9999-9999-999999999999");
    stubFor(get(urlEqualTo("/api/v1/products/" + productId))
        .withHeader("X-API-Key", equalTo(apiKey)).willReturn(aResponse().withStatus(404)));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port(), apiKey);

    Optional<ProductClientPort.ProductDto> result = client.getProductById(productId);

    assertTrue(result.isEmpty());
  }

  @Test
  void getProductById_ShouldRetryAndSucceed_WhenFirst500Then200() {
    UUID productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    stubFor(get(urlEqualTo("/api/v1/products/" + productId)).inScenario("retry")
        .whenScenarioStateIs("Started").withHeader("X-API-Key", equalTo(apiKey))
        .willReturn(aResponse().withStatus(500)).willSetStateTo("second"));

    stubFor(get(urlEqualTo("/api/v1/products/" + productId)).inScenario("retry")
        .whenScenarioStateIs("second").withHeader("X-API-Key", equalTo(apiKey))
        .willReturn(okJson("{\"data\":{\"id\":\"" + productId
            + "\",\"attributes\":{\"nombre\":\"P2\",\"precio\":1.0}}}")));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port(), apiKey);

    Optional<ProductClientPort.ProductDto> result = client.getProductById(productId);

    assertTrue(result.isPresent());
    assertEquals("P2", result.get().getNombre());
  }

  @Test
  void getProductById_ShouldThrowException_WhenAlways500() {
    UUID productId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    stubFor(get(urlEqualTo("/api/v1/products/" + productId))
        .withHeader("X-API-Key", equalTo(apiKey))
        .willReturn(aResponse().withStatus(500)));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port(), apiKey);

    assertThrows(RuntimeException.class, () -> client.getProductById(productId));
  }
}
