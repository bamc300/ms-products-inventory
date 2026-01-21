package com.linktic.msproducts.domain.port.out;

import com.linktic.msproducts.domain.model.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
  Product save(Product product);

  Optional<Product> findById(UUID id);

  java.util.List<Product> findAll();
}
