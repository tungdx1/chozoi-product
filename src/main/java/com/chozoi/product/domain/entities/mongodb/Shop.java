package com.chozoi.product.domain.entities.mongodb;

import com.chozoi.product.domain.entities.types.ShopRanking;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "shops.shop")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

  @Id
  private Integer id;

  private String name;

  private String email;

  private String contactName;

  private String phoneNumber;
  private String freeShipStatus;

  @Builder.Default
  private String tag = String.valueOf(ShopRanking.NORMAL);

  private String type;

  @JsonInclude(Include.NON_EMPTY)
  private List<Province> provinces;

  private String pageUrl;

  private String imgAvatarUrl;

  private String imgCoverUrl;

  private OfficialTemplate officialTemplate;

  private Boolean isLock;

  private Long createdAt;

  private Long updatedAt;

  @Data
  @Builder
  public static class OfficialTemplate {
    private Integer id;
    private String name;
    private String mainDesktopBanner;
    private String mainMobileBanner;
    private String desktopLogo;
    private String mobileLogo;
  }
}
