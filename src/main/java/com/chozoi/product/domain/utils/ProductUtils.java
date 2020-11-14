package com.chozoi.product.domain.utils;

import com.chozoi.product.domain.entities.elasticsearch.AuctionParticipantEs;
import com.chozoi.product.domain.entities.postgres.AuctionParticipantFlashBid;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProductUtils {
  public static List<ProductType> AUCTION_TYPE =
      Arrays.asList(ProductType.AUCTION, ProductType.AUCTION_SALE, ProductType.AUCTION_FLASH_BID);

  public static List<String> AUCTION_TYPE_STR =
      Arrays.asList("AUCTION_SALE", "AUCTION", "AUCTION_FLASH_BID");

  public static List<String> PRODUCT_TYPE_STR =
      Arrays.asList("CLASSIFIER", "PROMOTION", "SPECIAL", "NORMAL");

  public static List<String> PRODUCT_ALL_TYPE_STR =
      Arrays.asList(
          "CLASSIFIER",
          "AUCTION_SALE",
          "AUCTION",
          "AUCTION_FLASH_BID",
          "PROMOTION",
          "SPECIAL",
          "NORMAL");
  public static List<ProductState> STATE_FOR_SELLER =
      new ArrayList<>(
          Arrays.asList(
              ProductState.PUBLIC,
              ProductState.PENDING,
              ProductState.READY,
              ProductState.DRAFT,
              ProductState.REJECT,
              ProductState.REPORT));

  /**
   * get list type for filter product
   *
   * @param flag : ALL, AUCTION, NORMAL
   * @return in list: NORMAL, CLASSIFIER, AUCTION, AUCTION_SALE
   */
  public static List<String> productType(String flag) {
    List<String> type = new ArrayList<>();
    if (flag.equals("ALL")) {
      type.add("NORMAL");
      type.add("CLASSIFIER");
      type.add("PROMOTION");
      type.add("SPECIAL");
      type.add("AUCTION");
      type.add("AUCTION_SALE");
    } else if (flag.equals("AUCTION")) {
      type.add("AUCTION");
      type.add("AUCTION_SALE");
      type.add("AUCTION_FLASH_BID");
    } else if (flag.equals("NORMAL")) {
      type.add("NORMAL");
      type.add("CLASSIFIER");
      type.add("PROMOTION");
      type.add("SPECIAL");
    }
    return type;
  }

  public static void setInventoryAuction(ProductType type, ProductVariant variant) {
    variant.getInventory().setInitialQuantity(0);
    if (type.equals(ProductType.AUCTION) || type.equals(ProductType.AUCTION_SALE))
      variant.getInventory().setInQuantity(1);
  }

  public static boolean isAuction(ProductType type) {
    return AUCTION_TYPE.contains(type);
  }

  public static List<AuctionParticipantEs> auctionParcitipantPgToEs(
      List<AuctionParticipantFlashBid> participantFlashBids) {
    List<AuctionParticipantEs> response = new ArrayList<>();
    participantFlashBids.forEach(
        a -> {
          AuctionParticipantEs auctionParticipantEs = new AuctionParticipantEs();
          auctionParticipantEs.setId(UUID.randomUUID());
          auctionParticipantEs.setSort(0);
          auctionParticipantEs.setAuctionId(a.getId().getAuctionId());
          auctionParticipantEs.setAutoBidId(a.getAutoBidId());
          auctionParticipantEs.setCreatedAt(
              a.getCreatedAt().atZone(ZoneId.of("GMT")).toEpochSecond() * 1000);
          auctionParticipantEs.setUpdatedAt(
              a.getUpdatedAt().atZone(ZoneId.of("GMT")).toEpochSecond() * 1000);
          auctionParticipantEs.setPhaseId(a.getId().getPhaseId());
          auctionParticipantEs.setInstantBidId(a.getInstantBidId());
          auctionParticipantEs.setLastMinuteBidId(a.getLastMinuteBidId());
          auctionParticipantEs.setState(String.valueOf(a.getState()));
          auctionParticipantEs.setUserId(a.getId().getUserId());
          response.add(auctionParticipantEs);
        });
    return response;
  }
}
