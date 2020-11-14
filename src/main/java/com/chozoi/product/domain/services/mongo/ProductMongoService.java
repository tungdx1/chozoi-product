package com.chozoi.product.domain.services.mongo;

import com.chozoi.product.app.responses.VariantResponse;
import com.chozoi.product.domain.entities.mongodb.*;
import com.chozoi.product.domain.entities.redis.AuctionResultRedis;
import com.chozoi.product.domain.repositories.mongodb.ProductLikeMDRepository;
import com.chozoi.product.domain.services.async.AsyncProductService;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductMongoService {
  @Autowired AsyncProductService asyncService;

  @Autowired private BuyerProductService buyerProductService;

  @Autowired private ProductLikeMDRepository productLikeMDRepository;

  public Map<String, Object> getById(long productId, String userId) throws Exception {
    ProductMongo productMongo;
    userId = ObjectUtils.defaultIfNull(userId, "guest");
    productMongo = getOnlyMongo(productId);
    if (!productMongo.getState().equals("PUBLIC")) throw new Exception("Sản phẩm id: " + productId + " không tồn tại");
    buyerProductService.createView(userId, productId);
    List<ProductImage> images =
        ObjectUtils.defaultIfNull(productMongo.getImages(), new ArrayList<>());
    images.sort(Comparator.comparing(ProductImage::getSort));
    productMongo.setImages(images);
    // get like status
    Map<String, Object> response = new HashMap<>();
    ProductLike productLike = productLikeMDRepository.findById(userId).orElse(new ProductLike());
    List<Long> likeIds = ObjectUtils.defaultIfNull(productLike.getProductIds(), new ArrayList<>());
    boolean liked = likeIds.contains(productId);
    response.put("product", productMongo);
    response.put("liked", liked);
    return response;
  }

  private ProductMongo getOne(Long productId) throws Exception {
    CompletableFuture<ProductMongo> product = asyncService.getProduct(productId);
    CompletableFuture<List<InventoryMongo>> inventory = asyncService.getInvetory(productId);
    CompletableFuture<ProductStatsMongo> stats = asyncService.getStats(productId);

    CompletableFuture<List<ProductImage>> productImageCompletableFuture =
        asyncService.getProductImage(productId);
    ProductMongo productMongo = product.get();
    List<InventoryMongo> inventories = inventory.get();
    ProductStatsMongo productStats = stats.get();
    List<ProductImage> images = productImageCompletableFuture.get();
    return handle(productMongo, inventories, productStats, images);
  }

  public List<VariantResponse> getVariant(String data)
      throws NotFoundException, ExecutionException, InterruptedException {
    List<Long> ids = new ArrayList<>();
    List<String> result = Arrays.asList(data.split(","));
    result.forEach(
        v -> {
          try {
            Long id = Long.parseLong(v);
            ids.add(id);
          } catch (Exception e) {
            //  Block of code to handle errors
          }
        });
    CompletableFuture<List<InventoryMongo>> futureInventory = asyncService.getOneInvetories(ids);
    CompletableFuture<List<ProductVariant>> futureVariant = asyncService.getVariants(ids);
    List<InventoryMongo> inventory = futureInventory.get();
    List<ProductVariant> variants = futureVariant.get();
    List<Long> productIds =
        variants.stream().map(ProductVariant::getProductId).collect(Collectors.toList());
    CompletableFuture<List<ProductMongo>> futureProduct = asyncService.getProducts(productIds);
    List<ProductMongo> products = futureProduct.get();
    List<VariantResponse> responses = new ArrayList<>();
    variants.forEach(
        variant -> {
          int quantity = 0;
          List<InventoryMongo> inventoryMongo =
              inventory.stream()
                  .filter(v -> v.getId().equals(variant.getId()))
                  .collect(Collectors.toList());
          List<ProductMongo> product =
              products.stream()
                  .filter(v -> v.getId().equals(variant.getProductId()))
                  .collect(Collectors.toList());
          if (!product.isEmpty()) if (product.get(0).getIsQuantityLimited()) {
              if (!inventoryMongo.isEmpty()) {
                  InventoryMongo i = inventoryMongo.get(0);
                  quantity = i.getInitialQuantity() + i.getInQuantity() - i.getOutQuantity();
              }
          } else quantity += 999;
          responses.add(
              new VariantResponse(
                  variant.getId(), variant.getPrice(), variant.getSalePrice(), quantity));
        });

    return responses;
  }

  private ProductMongo getOnlyMongo(Long productId) throws Exception {
    CompletableFuture<ProductMongo> product = asyncService.getProductMongo(productId);
    CompletableFuture<List<InventoryMongo>> inventory = asyncService.getInvetoryMongo(productId);
    CompletableFuture<ProductStatsMongo> stats = asyncService.getStatsMongo(productId);
    CompletableFuture<List<ProductImage>> productImageCompletableFuture =
        asyncService.getProductImageMongo(productId);
    ProductMongo productMongo = product.get();
    List<InventoryMongo> inventories = inventory.get();
    ProductStatsMongo productStats = stats.get();
    List<ProductImage> images = productImageCompletableFuture.get();
    return handle(productMongo, inventories, productStats, images);
  }

  private ProductMongo handle(
      ProductMongo productMongo,
      List<InventoryMongo> inventories,
      ProductStatsMongo productStats,
      List<ProductImage> images)
      throws Exception {
    if (productMongo.getType().equals("AUCTION") || productMongo.getType().equals("AUCTION_SALE")) {
      CompletableFuture<AuctionResultRedis> auctionResultCompletableFuture =
          asyncService.getAuctionResult(productMongo.getId());
      AuctionResultRedis auctionResult = auctionResultCompletableFuture.get();
      AuctionMongo auction = productMongo.getAuction();
      auction.setResult(auctionResult);
      productMongo.setAuction(auction);
    }
    List<ProductVariant> variants = productMongo.getVariants();
    if (!Objects.isNull(variants)) variants.forEach(
            variant -> {
                List<InventoryMongo> inventoryMongoList =
                        inventories.stream()
                                .filter(inven -> inven.getId().equals(variant.getId()))
                                .collect(Collectors.toList());
                InventoryMongo inventoryMongo = new InventoryMongo();
                try {
                    inventoryMongo = inventoryMongoList.get(0);
                } catch (Exception ignored) {
                    inventoryMongo.setInQuantity(0);
                    inventoryMongo.setInitialQuantity(0);
                    inventoryMongo.setOutQuantity(0);
                    inventoryMongo.setProductId(productMongo.getId());
                }
                variant.setInventory(inventoryMongo);
            });
    productMongo.setVariants(variants);
    productMongo.setStats(productStats);
    productMongo.setImages(images);
    return productMongo;
  }
}
