package com.linktic.msinventory.domain.port.out;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

public interface ProductClientPort {
    Optional<ProductDto> getProductById(UUID id);

    @Data
    @Builder
    class ProductDto {
        private UUID id;
        private String nombre;
        private BigDecimal precio;
    }
}
