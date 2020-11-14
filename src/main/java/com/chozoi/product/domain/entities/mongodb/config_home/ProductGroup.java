package com.chozoi.product.domain.entities.mongodb.config_home;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public abstract class ProductGroup {
  @Id private Integer id;
  private String name;
  private Rules rules;
  private String type;
  private Long updatedAt;
  private Long createdAt;

  @Data
  public static class Rules {
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
}
