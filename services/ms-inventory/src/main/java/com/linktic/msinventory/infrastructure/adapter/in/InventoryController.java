package com.linktic.msinventory.infrastructure.adapter.in;

import com.linktic.msinventory.application.dto.InventoryResponseDto;
import com.linktic.msinventory.application.dto.PurchaseRequestDto;
import com.linktic.msinventory.application.dto.PurchaseResponseDto;
import com.linktic.msinventory.application.dto.UpdateInventoryRequestDto;
import com.linktic.msinventory.application.mapper.InventoryDtoMapper;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.model.Purchase;
import com.linktic.msinventory.domain.port.in.GetInventoryByProductIdUseCase;
import com.linktic.msinventory.domain.port.in.PurchaseUseCase;
import com.linktic.msinventory.domain.port.in.UpdateInventoryUseCase;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

/**
 * Controlador REST para la gestión de inventario. Provee endpoints para consultar stock, actualizar
 * inventario y procesar compras.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory Management APIs")
public class InventoryController {

  private final GetInventoryByProductIdUseCase getInventoryUseCase;
  private final UpdateInventoryUseCase updateInventoryUseCase;
  private final PurchaseUseCase purchaseUseCase;
  private final InventoryDtoMapper mapper;

  @GetMapping(value = "/inventory/{productId}", produces = "application/vnd.api+json")
  @Operation(summary = "Get inventory by Product ID")
  public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable UUID productId) {
    Inventory inventory = getInventoryUseCase.getInventoryByProductId(productId);
    return ResponseEntity.ok(mapper.toResponse(inventory));
  }

  @PatchMapping(value = "/inventory/{productId}", consumes = "application/vnd.api+json",
      produces = "application/vnd.api+json")
  @Operation(summary = "Update inventory stock")
  public ResponseEntity<InventoryResponseDto> updateInventory(@PathVariable UUID productId,
      @Valid @RequestBody UpdateInventoryRequestDto request) {
    Inventory inventory = updateInventoryUseCase.updateStock(productId,
        request.getData().getAttributes().getCantidad());
    return ResponseEntity.ok(mapper.toResponse(inventory));
  }

  @PostMapping(value = "/purchases", consumes = "application/vnd.api+json",
      produces = "application/vnd.api+json")
  @Operation(summary = "Execute purchase")
  @ApiResponse(responseCode = "201", description = "Purchase successful",
      content = @Content(mediaType = "application/vnd.api+json",
          schema = @Schema(implementation = PurchaseResponseDto.class)))
  public ResponseEntity<PurchaseResponseDto> purchase(
      @Valid @RequestBody PurchaseRequestDto request) {
    Purchase purchase = purchaseUseCase.purchase(request.getData().getAttributes().getProductId(),
        request.getData().getAttributes().getQuantity());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(purchase));
  }
}
