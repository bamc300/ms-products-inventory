package com.linktic.msproducts.domain.port.in;

import com.linktic.msproducts.domain.model.Product;
import java.util.List;

public interface GetAllProductsUseCase {
  List<Product> getAllProducts();
}
