package com.chozoi.product.data.response.home;

import com.chozoi.product.data.response.CategoryResponse;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.data.response.ShopResponse;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeData {
  private int id;
  private String title;
  private String titleLink;
  private String titleScreen;
  @Builder.Default private SuperData data = new SuperData();
  @Builder.Default private String banner;
  @Builder.Default private String bannerApp;
  @Builder.Default private String bannerScreen;
  @Builder.Default private String bannerLink;
  @Builder.Default private String background;
  @Builder.Default private String tab = "HOT"; // HOT,NEW, MISS
  @Builder.Default private String template = "NORMAL";
  @Builder.Default private List<LayoutBlock.Task> tasks;

  @Builder.Default
  private String typeData =
      "PRODUCT"; // PRODUCT, KEYWORD, SHOP, SPOTLIGHT, AUCTION, PRODUCT_SHOP, OFFICIAL

  @Builder.Default private int sort;

  @Data
  public static class SuperData {
    private List<ProductsPublicResponse> products;
    private List<Spotlight> spotlights;
    private List<ShopResponse> shops;
    private List<KeyWord> keywords;
    private List<CategoryResponse> categories;
    private OfficialStore officialStores;
  }

  @Data
  public static class KeyWord {
    private String key;
    private String imageUrl;
    private String imageUrlApp;
    private String link;
    private String linkId;
    private String screen;
    private String searchValue;
  }

  @Data
  public static class Spotlight {
    private String key;
    private String imageUrl;
    private String imageUrlApp;
    private String screen;
    private String link;
    private String linkId;
  }

  @Data
  public static class OfficialStore {
    private List<Store> priority;
    private List<Store> nonPriority;
    public List<LayoutBlock.Stores.Banner> bannerList;
  }

  @Data
  public static class Store {
    private Integer id;
    private String name;
    private String title;
    private String banner;
    private String bannerApp;
    private String avatar;
    private String avatarApp;
  }
}
