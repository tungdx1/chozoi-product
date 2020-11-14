package com.chozoi.product.domain.exceptions;

import lombok.Data;

@Data
public class ProductNotFoundException extends Exception {

  private static final long serialVersionUID = 3009346076966514674L;
  private Integer status;

  public ProductNotFoundException() {
    super(ExceptionMessage.PRODUCT_NOT_FOUND);
    this.status = 404;
  }
}
