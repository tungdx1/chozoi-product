package com.chozoi.product.domain.exceptions;

public class ChozoiExcention extends Exception {
  protected Integer integer;

  public ChozoiExcention(String message) {
    super(message);
  }
}
