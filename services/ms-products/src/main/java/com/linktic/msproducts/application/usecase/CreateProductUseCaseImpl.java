package com.linktic.msproducts.application.usecase;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.in.CreateProductUseCase;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

  private final ProductRepositoryPort productRepositoryPort;

  @Override
  public Product createProduct(Product product) {
    return productRepositoryPort.save(product);
  }
}
