package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
public class ProductStateDto {
  @NotNull
  private ProductState state;

  @NotNull
  private Long productId;

  @NotNull
  private Integer userSystemId;

  private Integer categoryId;

  private String description;

  private String solution;

  @NotNull
  private Integer updatedVersion;

  // check classifier
  @AssertTrue(message = "description and solution not null")
  @JsonIgnore
  protected boolean isValidDescriptionNotNull() {
    if ( state == ProductState.REJECT || state == ProductState.REPORT || state == ProductState.REJECTPRODUCT ) {
      if ( description == null || solution == null ) return false;
      return true;
    }
    return true;
  }
}
