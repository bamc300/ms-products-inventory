package com.linktic.msproducts.application.mapper;

import com.linktic.msproducts.application.dto.CreateProductRequestDto;
import com.linktic.msproducts.application.dto.ProductResponseDto;
import com.linktic.msproducts.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductDtoMapper {

        public Product toDomain(CreateProductRequestDto request) {
                return Product.builder().nombre(request.getData().getAttributes().getNombre())
                                .precio(request.getData().getAttributes().getPrecio())
                                .descripcion(request.getData().getAttributes().getDescripcion())
                                .build();
        }

        public ProductResponseDto toResponse(Product product) {
                return ProductResponseDto.builder().data(ProductResponseDto.ProductData.builder()
                                .type("product")
                                .id(product.getId() != null ? product.getId().toString() : null)
                                .attributes(ProductResponseDto.ProductAttributes.builder()
                                                .nombre(product.getNombre())
                                                .precio(product.getPrecio())
                                                .descripcion(product.getDescripcion()).build())
                                .build()).build();
        }
}
