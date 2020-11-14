package com.chozoi.product.app.dtos;

import com.chozoi.product.data.request.ProductClassifier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Log4j2
public class UpdatePartialProductDTO {
  protected Long id;

  @Positive(message = "weight must be positive")
  @JsonProperty("weight")
  protected Integer weight;

  protected Boolean isQuantityLimited;

  @NotNull()
  @NotEmpty()
  @JsonProperty("packing_size")
  protected Integer[] packingSize;

  @JsonProperty("free_ship_status")
  protected Boolean freeShipStatus;


  protected List<ProductClassifier> classifiers;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  protected List<@Valid VariantDTO> variants;

  // check count variants
  @AssertTrue(message = "variants size false")
  @JsonIgnore
  protected boolean isValidVariants() {
    return variants.size() >= 1;
  }

  // check count variants
  @AssertTrue(message = "Packing size is 3")
  @JsonIgnore
  protected boolean packingSize() {
    List<Integer> list = new ArrayList<>();
    for (Integer t : packingSize) list.add(t);
    if ( list.size() != 3 ) return false;
    return true;
  }
}
