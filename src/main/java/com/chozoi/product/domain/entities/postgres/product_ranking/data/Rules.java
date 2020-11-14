package com.chozoi.product.domain.entities.postgres.product_ranking.data;

import lombok.Data;

import java.util.List;

@Data
public class Rules {
  private List<Integer> categories;
  private List<Condition> products;

  @Data
  public static class Condition {
    private String sort;
    private String type;
    private String state;
    private String condition;
  }
}
