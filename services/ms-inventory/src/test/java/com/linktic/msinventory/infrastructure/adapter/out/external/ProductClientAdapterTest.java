package com.linktic.msinventory.infrastructure.adapter.out.external;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductClientAdapterTest {

  private WireMockServer wireMockServer;
  private String token = "Bearer test-token";

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
    wireMockServer.start();
    configureFor("localhost", wireMockServer.port());

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", token);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @AfterEach
  void tearDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void getProductById_ShouldReturnProduct_When200() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    stubFor(get(urlEqualTo("/api/v1/products/" + productId))
        .withHeader("Authorization", equalTo(token)).willReturn(okJson("{\"data\":{\"id\":\""
            + productId + "\",\"attributes\":{\"nombre\":\"P1\",\"precio\":50.0}}}")));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port());

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
        .withHeader("Authorization", equalTo(token)).willReturn(aResponse().withStatus(404)));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port());

    Optional<ProductClientPort.ProductDto> result = client.getProductById(productId);

    assertTrue(result.isEmpty());
  }

  @Test
  void getProductById_ShouldRetryAndSucceed_WhenFirst500Then200() {
    UUID productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    stubFor(get(urlEqualTo("/api/v1/products/" + productId)).inScenario("retry")
        .whenScenarioStateIs("Started").withHeader("Authorization", equalTo(token))
        .willReturn(aResponse().withStatus(500)).willSetStateTo("second"));

    stubFor(get(urlEqualTo("/api/v1/products/" + productId)).inScenario("retry")
        .whenScenarioStateIs("second").withHeader("Authorization", equalTo(token))
        .willReturn(okJson("{\"data\":{\"id\":\"" + productId
            + "\",\"attributes\":{\"nombre\":\"P2\",\"precio\":1.0}}}")));

    ProductClientPort client =
        new ProductClientAdapter("http://localhost:" + wireMockServer.port());

    Optional<ProductClientPort.ProductDto> result = client.getProductById(productId);

    assertTrue(result.isPresent());
    assertEquals(productId, result.get().getId());
    verify(2, getRequestedFor(urlEqualTo("/api/v1/products/" + productId)));
  }

  @Test
  void getProductById_ShouldThrowAfterRetries_WhenConnectionRefused() throws Exception {
    int port;
    try (ServerSocket socket = new ServerSocket(0)) {
      port = socket.getLocalPort();
    }

    ProductClientPort client = new ProductClientAdapter("http://localhost:" + port);

    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> client.getProductById(productId));
    assertFalse(ex.getMessage().isBlank());
  }
}
