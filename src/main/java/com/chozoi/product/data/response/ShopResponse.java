package com.chozoi.product.data.response;

import com.chozoi.product.domain.entities.mongodb.ShopStats;
import lombok.Data;

import java.util.List;

@Data
public class ShopResponse {
  private Long id;
  private String name;
  private String imgAvatarUrl;
  private String freeShipStatus;

  private String tag = "NORMAL";

  private List<Provinces> provinces;
  private ShopStats stats;

  @Data
  public static class Provinces {
    private Integer id;
    private String provinceName;
  }
}
