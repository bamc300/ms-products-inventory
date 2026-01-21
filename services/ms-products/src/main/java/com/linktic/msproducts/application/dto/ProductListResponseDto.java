package com.linktic.msproducts.application.dto;

import com.linktic.msproducts.application.dto.ProductResponseDto.ProductData;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponseDto {
  private List<ProductData> data;
}
