package com.chozoi.product.app.responses;

import com.chozoi.product.domain.entities.postgres.product_ranking.ConfigLayoutBlock;
import com.chozoi.product.domain.factories.BeanMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigHomeResponse<T> extends ProductsPublicResponseAbs {
  private Integer id;
  private String title;
  private String banner;
  private String bannerLink;
  private List<T> products;

  @Autowired
  private BeanMapper modelMapper;

  public ConfigHomeResponse(ConfigLayoutBlock config) {
    id = config.getId();
    title = config.getTitle();
    banner = config.getBanner();
    bannerLink = config.getBannerLink();
    products = new ArrayList<>();
  }

  public void inferpropreties(List<T> products, Class<T> tClass) {
    this.products = ProductsPublicResponseAbs(products, tClass);
  }
}
