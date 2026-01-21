package com.linktic.msinventory.infrastructure.adapter.out.external;

import com.linktic.msinventory.domain.exception.ExternalServiceException;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import com.linktic.msinventory.domain.port.out.ProductClientPort.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductClientAdapter implements ProductClientPort {

  private final RestClient restClient;
  private final String apiKey;
  private static final int MAX_RETRIES = 2;

  public ProductClientAdapter(@Value("${products.base-url}") String baseUrl,
      @Value("${application.security.api-key}") String apiKey) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2000);
    factory.setReadTimeout(2000);

    this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    this.apiKey = apiKey;
  }

  @Override
  public Optional<ProductDto> getProductById(UUID id) {
    int attempt = 0;
    while (attempt <= MAX_RETRIES) {
      try {
        RestClient.RequestHeadersSpec<?> request =
            restClient.get().uri("/api/v1/products/{id}", id);
        request.header("X-API-Key", apiKey);

        return Optional.ofNullable(request.retrieve().body(ProductResponseWrapper.class))
            .map(wrapper -> ProductDto.builder().id(UUID.fromString(wrapper.getData().getId()))
                .nombre(wrapper.getData().getAttributes().getNombre())
                .precio(wrapper.getData().getAttributes().getPrecio()).build());

      } catch (HttpClientErrorException.NotFound e) {
        return Optional.empty();
      } catch (ResourceAccessException | HttpServerErrorException e) {
        attempt++;
        if (attempt > MAX_RETRIES) {
          throw new RuntimeException(
              "Timeout calling Products Service after " + MAX_RETRIES + " retries", e);
        }
        try {
          Thread.sleep(100L * attempt); // Simple backoff: 100ms, 200ms
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException("Interrupted during retry", ie);
        }
      } catch (Exception e) {
        throw new RuntimeException("Error calling Products Service", e);
      }
    }
    return Optional.empty();
  }

  @lombok.Data
  static class ProductResponseWrapper {
    private ProductData data;
  }

  @lombok.Data
  static class ProductData {
    private String id;
    private ProductAttributes attributes;
  }

  @lombok.Data
  static class ProductAttributes {
    private String nombre;
    private java.math.BigDecimal precio;
  }
}
