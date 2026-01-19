package com.linktic.msproducts.infrastructure.adapter.in;

import com.linktic.msproducts.application.dto.CreateProductRequestDto;
import com.linktic.msproducts.application.dto.ProductResponseDto;
import com.linktic.msproducts.application.mapper.ProductDtoMapper;
import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.in.CreateProductUseCase;
import com.linktic.msproducts.domain.port.in.GetProductByIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

/**
 * Controlador REST para la gestión de productos. Provee endpoints para crear y consultar productos.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product Management APIs")
public class ProductController {

  private final CreateProductUseCase createProductUseCase;
  private final GetProductByIdUseCase getProductByIdUseCase;
  private final ProductDtoMapper mapper;

  @PostMapping(consumes = "application/vnd.api+json", produces = "application/vnd.api+json")
  @Operation(summary = "Create a new product")
  @ApiResponse(responseCode = "201", description = "Product created",
      content = @Content(mediaType = "application/vnd.api+json",
          schema = @Schema(implementation = ProductResponseDto.class)))
  public ResponseEntity<ProductResponseDto> createProduct(
      @Valid @RequestBody CreateProductRequestDto request) {
    Product product = mapper.toDomain(request);
    Product created = createProductUseCase.createProduct(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
  }

  @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
  @Operation(summary = "Get product by ID")
  @ApiResponse(responseCode = "200", description = "Product found",
      content = @Content(mediaType = "application/vnd.api+json",
          schema = @Schema(implementation = ProductResponseDto.class)))
  public ResponseEntity<ProductResponseDto> getProductById(@PathVariable UUID id) {
    Product product = getProductByIdUseCase.getProductById(id);
    return ResponseEntity.ok(mapper.toResponse(product));
  }
}
