package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockGroupMongo;
import com.chozoi.product.domain.entities.mongodb.config_home.ProductGroup;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductStaticService {
  public static List<String> TYPE_AUCTION_STRING =
      Arrays.asList(String.valueOf(ProductType.AUCTION_SALE), String.valueOf(ProductType.AUCTION));

  public static List<ProductEs> removeAuctionStopped(List<ProductEs> productList) {
    List<ProductEs> products = new ArrayList<>();
    productList.forEach(
        productEs -> {
          if (productEs.getType().equals("AUCTION") || productEs.getType().equals("AUCTION_SALE")) {
            if (productEs.getAuction().getState().equals("BIDING")) products.add(productEs);
          } else products.add(productEs);
        });
    return products;
  }

  public static List<ProductEs> removeAuctionStopped(Iterable<ProductEs> productList) {
    List<ProductEs> products = new ArrayList<>();
    productList.forEach(
        productEs -> {
          if (productEs.getType().equals("AUCTION") || productEs.getType().equals("AUCTION_SALE") || productEs.getType().equals("AUCTION_FLASH_BID")) {
              long currentTime = System.currentTimeMillis();
            if (productEs.getAuction().getState().equals("BIDING") && Long.parseLong(String.valueOf(productEs.getAuction().getTimeEnd())) > currentTime) products.add(productEs);
          } else products.add(productEs);
        });
    return products;
  }

  public static boolean checkAliveAi(LayoutBlock config) {
    if (config.getProductGroups() != null && config.getProductGroups().size() > 0) {
        for (LayoutBlockGroupMongo layoutBlockGroupMongo: config.getProductGroups()) {
            ProductGroup productGroup = layoutBlockGroupMongo.getGroup();
            if (productGroup != null && productGroup.getType().equals("AI")) {
                return true;
            }
        }
    }
    return false;
  }

  public static String auctionState(String auction_state) {
    List<String> state =
        Arrays.asList(
            String.valueOf(ProductAuctionState.BIDING),
            String.valueOf(ProductAuctionState.STOPPED),
            String.valueOf(ProductAuctionState.WAITING));
    return auction_state;
  }
}
