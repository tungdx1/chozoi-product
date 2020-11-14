package com.chozoi.product.domain.services.static_service;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.producers.content.AuctionData;
import com.chozoi.product.domain.utils.ProductUtils;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductStaticService {
  public static Product productInList(Long id, List<Product> products) {
    try {
      List<Product> productList =
          products.stream()
              .filter(product1 -> product1.getId().equals(id))
              .collect(Collectors.toList());
      return productList.get(0);
    } catch (Exception e) {
    }
    return null;
  }

  public static AuctionData builderAuction(Product product) {
    LocalDateTime a = product.getAuction().getTimeEnd();
    long timeEnd = a.toEpochSecond(ZoneOffset.UTC);
    long nowSecond = System.currentTimeMillis() / 1000;
    long time = timeEnd - nowSecond;
    long remainTime = time < 0 ? 0 : time;
    AuctionData auctionData = new AuctionData();
    auctionData.setRemainTime(remainTime);
    auctionData.setCurrentPrice(product.getAuction().getResult().getCurrentPrice());
    auctionData.setBidPrice(product.getAuction().getResult().getCurrentPrice());
    auctionData.setBidCount((long) product.getAuction().getResult().getBidsCount());
    return auctionData;
  }

  public static ProductState specifiedStatePublic(Product product) {
    return product.getState() == ProductState.PUBLIC || product.getAutoPublic()
        ? ProductState.PUBLIC
        : ProductState.READY;
  }

  public static boolean isAuction(Product product) {
    if (ProductUtils.AUCTION_TYPE.contains(product.getType())) return true;
    return false;
  }
}
