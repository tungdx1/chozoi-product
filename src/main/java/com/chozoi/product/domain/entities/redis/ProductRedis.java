package com.chozoi.product.domain.entities.redis;

import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.data.request.ProductClassifier;
import com.chozoi.product.domain.entities.mongodb.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@RedisHash(value = "Product", timeToLive = 86400)
public class ProductRedis implements Serializable {
    public Long id;

    public String name;

    public List<Integer> shippingPartnerIds;

    public List<Integer> packingSize;

    public Long price;

    public Long salePrice;

    public String condition;

    public String sku;

    public String currency;

    public Integer weight;

    public Integer remainingQuantity;

    public Integer soldQuantity;

    public String description;

    public String descriptionPinking;

    public String descriptionPinkingin;

    public String descriptionPinkingout;

    public List<String> videos;

    public String type;

    public Boolean isQuantityLimited;

    public String state;

    public List<AttributeProduct> attributes;

    public Category category;
    public List<ProductImage> images;
    public Shop shop;
    public AuctionMongo auction;
    public Promotion promotion;
    public List<ProductVariantRedis> variants;
    public ProductStatsRedis stats;
    public Long createdAt;
    public Long updatedAt;
    private List<ProductClassifier> classifiers;
}
