package com.chozoi.product.domain.values;

import com.chozoi.product.domain.entities.postgres.EventContent;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductEventContent extends EventContent {

    private Long id;
    private Integer shopId;
    private Integer categoryId;
    private ProductType type;
    private ProductState state;
    private ProductState preState;
    private Auction auction;
    private List<ProductVariant> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    public static class ProductVariant {
        private Long id;
        private Long price;
        private Long salePrice;
        private Inventory inventory;
    }

    @Data
    @NoArgsConstructor
    public static class Inventory {
        private Integer initialQuantity;
        private Integer inQuantity;
        private Integer outQuantity;
    }

    @Data
    @NoArgsConstructor
    public static class Auction {
        private Long id;
        private ProductAuctionState state;
        private LocalDateTime timeStart;
        private LocalDateTime timeEnd;
        private long priceStep;
        private long startPrice;
        private Long buyNowPrice;
        private long originalPrice;
        private Integer timeDuration;
        private AuctionResult result;
    }

    @Data
    @NoArgsConstructor
    public static class AuctionResult {
        private Long id;
        private Auction auction;
        private Integer biddersCount;
        private Integer bidsCount;
        private Long currentPrice;
        private Long ceilingPrice;
        private Integer winnerId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    }

}
