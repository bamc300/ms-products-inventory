package com.linktic.msproducts.application.usecase;

import com.linktic.msproducts.domain.exception.ProductNotFoundException;
import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.in.GetProductByIdUseCase;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProductByIdUseCaseImpl implements GetProductByIdUseCase {

  private final ProductRepositoryPort productRepositoryPort;

  @Override
  public Product getProductById(UUID id) {
    return productRepositoryPort.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
  }
}
