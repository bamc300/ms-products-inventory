package com.linktic.msproducts.domain.port.in;

import com.linktic.msproducts.domain.model.Product;
import java.util.UUID;

public interface GetProductByIdUseCase {
  Product getProductById(UUID id);
}
