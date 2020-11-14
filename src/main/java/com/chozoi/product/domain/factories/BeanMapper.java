package com.chozoi.product.domain.factories;

import com.chozoi.product.domain.entities.mongodb.*;
import com.chozoi.product.domain.entities.postgres.Inventory;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.redis.*;
import com.chozoi.product.domain.values.InventoryEventContent;
import com.chozoi.product.domain.values.ProductEventContent;
import com.chozoi.product.domain.values.ProductViewEventContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BeanMapper {


    @Mapping(target = "shopId", source = "shop.id")
    @Mapping(target = "categoryId", source = "category.id")
    ProductEventContent productToEventContent(Product product);

    @Mapping(target = "shopId", source = "shop.id")
    @Mapping(target = "categoryId", source = "category.id")
    ProductViewEventContent productToViewEventContent(Product product);

    InventoryEventContent inventoryToEventContent(Inventory inventory);

    ProductStatsRedis productStatsMongoToRedis(ProductStatsMongo product);

    ProductStatsMongo productStatsRedisToMongo(ProductStatsRedis productRedis);

    ProductMongo productRedisToMongo(ProductRedis productRedis);

    ProductRedis productMongoToRedis(ProductMongo productRedis);

    List<InventoryRedis> inventoryMongoToRedis(List<InventoryMongo> inventoryMongoList);

    List<InventoryMongo> inventoryRedisToMongo(List<InventoryRedis> inventoryList);

    List<ProductImage> imageRedisToMongo(List<ProductImageRedis> productImageRedis);

    List<ProductImageRedis> imageMongoToRedis(List<ProductImage> productImageRedis);

    Shop shopRedisToMongo(ShopRedis shopRedis);

    ShopRedis shopMongoToRedis(Shop shop);

    ShopStatsRedis shopStatsMongoToRedis(ShopStats shopStats);

    ShopStats shopStatsRedisToMongo(ShopStatsRedis shop);

    AuctionResultMongo auctionResultRedisToMongo(AuctionResultRedis auctionResultRedis);

    AuctionResultRedis auctionResultMongoToRedis(AuctionResultMongo auctionResultMongo);


}
