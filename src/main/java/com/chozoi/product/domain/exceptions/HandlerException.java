package com.chozoi.product.domain.exceptions;

import lombok.Data;

@Data
public class HandlerException extends ChozoiExcention {
  private static final long serialVersionUID = 3009346076966514674L;
  private Integer status;

  public HandlerException(String message) {
    super(message);
    this.status = 404;
  }

  public HandlerException(String message, Integer status) {
    super(message);
    this.status = status;
  }
}
