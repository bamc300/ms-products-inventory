package com.linktic.msproducts.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
  private List<ErrorDetail> errors;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ErrorDetail {
    private String status;
    private String title;
    private String detail;
  }
}
