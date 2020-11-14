package com.chozoi.product.data.response;

import lombok.Data;

import java.util.List;

@Data
public class RecursiveData {
  private Boolean status = false;
  private List<Long> ids;

  public RecursiveData(boolean status, List<Long> ids) {
    this.ids = ids;
    this.status = status;
  }
}
