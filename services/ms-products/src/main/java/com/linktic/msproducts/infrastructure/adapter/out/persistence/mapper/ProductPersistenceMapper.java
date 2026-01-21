package com.linktic.msproducts.infrastructure.adapter.out.persistence.mapper;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

  public ProductEntity toEntity(Product product) {
    if (product == null)
      return null;
    return ProductEntity.builder().id(product.getId()).nombre(product.getNombre())
        .precio(product.getPrecio()).descripcion(product.getDescripcion()).build();
  }

  public Product toDomain(ProductEntity entity) {
    if (entity == null)
      return null;
    return Product.builder().id(entity.getId()).nombre(entity.getNombre())
        .precio(entity.getPrecio()).descripcion(entity.getDescripcion()).build();
  }
}
