package com.linktic.msproducts.application.usecase;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.in.GetAllProductsUseCase;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllProductsUseCaseImpl implements GetAllProductsUseCase {

  private final ProductRepositoryPort productRepositoryPort;

  @Override
  public List<Product> getAllProducts() {
    return productRepositoryPort.findAll();
  }
}
