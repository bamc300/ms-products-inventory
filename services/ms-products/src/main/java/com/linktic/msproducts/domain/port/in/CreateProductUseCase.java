package com.linktic.msproducts.domain.port.in;

import com.linktic.msproducts.domain.model.Product;

public interface CreateProductUseCase {
  Product createProduct(Product product);
}
