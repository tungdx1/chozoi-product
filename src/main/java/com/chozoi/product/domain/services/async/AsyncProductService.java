package com.chozoi.product.domain.services.async;

import com.chozoi.product.domain.constants.IndexEsConstant;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.*;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import com.chozoi.product.domain.entities.redis.AuctionResultRedis;
import com.chozoi.product.domain.entities.redis.ProductImageRedis;
import com.chozoi.product.domain.entities.redis.ProductStatsRedis;
import com.chozoi.product.domain.factories.BeanMapper;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.mongodb.*;
import com.chozoi.product.domain.repositories.postgres.AuctionOnlyRepository;
import com.chozoi.product.domain.repositories.redis.*;
import com.chozoi.product.domain.services.elasticsearch.HomeService;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Log4j2
public class AsyncProductService {
  @Autowired private ProductMDRepository productRepository;

  @Autowired private InventoryMDRepository inventoryMDRepository;

  @Autowired private ProductVariantMDRepository productVariantMDRepository;

  @Autowired private ProductStatsMDRepository productStatsMDRepository;

  @Autowired private AuctionResultMDRepository auctionResultMDRepository;

  @Autowired private ProductEsRepository productEsRepository;

  @Autowired private AuctionOnlyRepository auctionOnlyRepository;

  // ===========REDIS==============//

  @Autowired private ProductStatsRedisRepository productStatsRepository;

  @Autowired private ProductRedisRepository productRedisRepository;

  @Autowired private InventoryRedisRepository inventoryRedisRepository;

  @Autowired private ProductImageRedisRepository imageRedisRepository;

  @Autowired private ProductImageMDRepository productImageMDRepository;

  @Autowired private AuctionResultRedisRepository auctionResultRedisRepository;

  @Autowired private BeanMapper beanMapper;

  private HomeService homeService;

  @Async
  public CompletableFuture<ProductStatsMongo> getStats(long productId)
      throws ExecutionException, InterruptedException {
    if (productStatsRepository.existsById(productId)) {
      ProductStatsRedis productStatsRedis =
          productStatsRepository.findById(productId).orElse(ProductStatsRedis.builder().build());
      return CompletableFuture.completedFuture(
          beanMapper.productStatsRedisToMongo(productStatsRedis));
    } else {
      ProductStatsMongo productStats =
          CompletableFuture.completedFuture(
                  productStatsMDRepository.findById(productId).orElse(new ProductStatsMongo()))
              .get();
      ProductStatsRedis productStatsRedis = beanMapper.productStatsMongoToRedis(productStats);
      productStatsRepository.save(productStatsRedis);
      return CompletableFuture.completedFuture(productStats);
    }
  }

  private Page<ProductEs> searchQuery(BoolQueryBuilder queryBuilder, Pageable pageable) {
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices(IndexEsConstant.PRODUCT_INDEX)
            .withTypes(IndexEsConstant.PRODUCT_TYPE)
            .withPageable(pageable)
            .build();
    return productEsRepository.search(searchQuery);
  }

  @Async
  public CompletableFuture<List<InventoryMongo>> getInvetory(long productId)
      throws ExecutionException, InterruptedException {
    //        List<InventoryRedis> inventory = inventoryRedisRepository.findByProductId(productId);
    List<InventoryMongo> inventoryMongoList =
        CompletableFuture.completedFuture(inventoryMDRepository.findAllByProductId(productId))
            .get();
    //        if (inventory.size() > 0) {
    //            return
    // CompletableFuture.completedFuture(beanMapper.inventoryRedisToMongo(inventory));
    //        } else {
    //            List<InventoryMongo> inventoryMongoList =
    // CompletableFuture.completedFuture(inventoryMDRepository.findAllByProductId(productId)).get();
    //            List<InventoryRedis> inventoryRedis =
    // beanMapper.inventoryMongoToRedis(inventoryMongoList);
    //            inventoryRedisRepository.saveAll(inventoryRedis);
    //            return CompletableFuture.completedFuture(inventoryMongoList);
    //        }
    return CompletableFuture.completedFuture(inventoryMongoList);
  }

  @Async
  public CompletableFuture<ProductMongo> getProduct(long productId)
          throws InterruptedException, ExecutionException, NotFoundException {
    //    ProductRedis productRedis =
    // productRedisRepository.findById(productId).orElse(ProductRedis.builder().build());
    //    if ( !Objects.isNull(productRedis.getId()) ) {
    //      ProductMongo productMongo = beanMapper.productRedisToMongo(productRedis);
    //      if ( Objects.isNull(productMongo.getAttributes()) ||
    // Objects.isNull(productMongo.getAttributes().get(0)) ) productMongo.setAttributes(new
    // ArrayList<>());
    //      return CompletableFuture.completedFuture(productMongo);
    //    } else {
    //      Optional<ProductMongo> product =
    // CompletableFuture.completedFuture(productRepository.findById(productId)).get();
    //      ProductMongo productMongo = product.get();
    //      ProductRedis productRedis1 = beanMapper.productMongoToRedis(product.get());
    //      productRedisRepository.save(productRedis1);
    //      return CompletableFuture.completedFuture(productMongo);
    //    }
    Optional<ProductMongo> productMongoOptional = productRepository.findById(productId);
    ProductMongo productMongo =
            productMongoOptional.orElseThrow(() -> new NotFoundException("Product not found"));

    if (productMongo.getAuction() == null) {
      Optional<AuctionOnly> auctionOnlyOptional = auctionOnlyRepository.findById(productMongo.getId());
      if (auctionOnlyOptional.isPresent()) {
        AuctionOnly auctionOnly = auctionOnlyOptional.get();
        AuctionMongo auctionMongo = new AuctionMongo();
        auctionMongo.asignFrom(auctionOnly);
        productMongo.setAuction(auctionMongo);
      }
    }

    return CompletableFuture.completedFuture(productMongo);
  }

  @Async
  public CompletableFuture<AuctionResultRedis> getAuctionResult(long id) throws Exception {
    if (auctionResultRedisRepository.existsById(id)) {
      AuctionResultRedis data =
          auctionResultRedisRepository.findById(id).orElse(AuctionResultRedis.builder().build());
      return CompletableFuture.completedFuture(data);
    } else {
      AuctionResultMongo data =
          auctionResultMDRepository.findById(id).orElse(AuctionResultMongo.create(id));
      AuctionResultRedis productStatsRedis = beanMapper.auctionResultMongoToRedis(data);
      auctionResultRedisRepository.save(productStatsRedis);
      return CompletableFuture.completedFuture(productStatsRedis);
    }
  }

  @Async
  public CompletableFuture<List<ProductImage>> getProductImage(long productId)
      throws ExecutionException, InterruptedException {
    List<ProductImageRedis> productImages = imageRedisRepository.findByProductId(productId);
    if (productImages.size() > 0) return CompletableFuture.completedFuture(beanMapper.imageRedisToMongo(productImages));
    else {
      List<ProductImage> productImages1 =
          CompletableFuture.completedFuture(productImageMDRepository.findByProductId(productId))
              .get();
      List<ProductImageRedis> productImageRedis = beanMapper.imageMongoToRedis(productImages1);
      imageRedisRepository.saveAll(productImageRedis);
      return CompletableFuture.completedFuture(productImages1);
    }
  }

  @Async
  public CompletableFuture<List<InventoryMongo>> getOneInvetories(List<Long> ids) {
    Iterable<InventoryMongo> inventory = inventoryMDRepository.findAllById(ids);
    List<InventoryMongo> inventoryMongos =
        StreamSupport.stream(inventory.spliterator(), false).collect(Collectors.toList());
    return CompletableFuture.completedFuture(inventoryMongos);
  }

  @Async
  public CompletableFuture<List<ProductVariant>> getVariants(List<Long> ids)
      throws NotFoundException {
    Iterable<ProductVariant> variants = productVariantMDRepository.findAllById(ids);
    List<ProductVariant> variantList =
        StreamSupport.stream(variants.spliterator(), false).collect(Collectors.toList());
    return CompletableFuture.completedFuture(variantList);
  }

  public CompletableFuture<List<ProductMongo>> getProducts(List<Long> productIds) {
    Iterable<ProductMongo> value = productRepository.findAllById(productIds);
    List<ProductMongo> products =
        StreamSupport.stream(value.spliterator(), false).collect(Collectors.toList());
    return CompletableFuture.completedFuture(products);
  }

  @Async
  public CompletableFuture<ProductStatsMongo> getStatsMongo(long productId)
      throws ExecutionException, InterruptedException {
    ProductStatsMongo productStats =
        CompletableFuture.completedFuture(
                productStatsMDRepository.findById(productId).orElse(new ProductStatsMongo()))
            .get();
    return CompletableFuture.completedFuture(productStats);
  }

  @Async
  public CompletableFuture<List<InventoryMongo>> getInvetoryMongo(long productId)
      throws ExecutionException, InterruptedException {
    List<InventoryMongo> inventoryMongoList =
        CompletableFuture.completedFuture(inventoryMDRepository.findAllByProductId(productId))
            .get();
    return CompletableFuture.completedFuture(inventoryMongoList);
  }

  @Async
  public CompletableFuture<ProductMongo> getProductMongo(long productId)
      throws InterruptedException, ExecutionException, NotFoundException {
    Optional<ProductMongo> productMongoOptional = productRepository.findById(productId);
    ProductMongo productMongo =
            productMongoOptional.orElseThrow(() -> new NotFoundException("Product not found"));

    if (productMongo.getShop() == null) {
      throw new NotFoundException("Lỗi dữ liệu");

    }

    if (productMongo.getShop().getIsLock() != null && productMongo.getShop().getIsLock()) {
      throw new NotFoundException("Gian hàng đang tạm khóa");
    }

    if (productMongo.getAuction() == null) {
      Optional<AuctionOnly> auctionOnlyOptional = auctionOnlyRepository.findById(productMongo.getId());
      if (auctionOnlyOptional.isPresent()) {
        AuctionOnly auctionOnly = auctionOnlyOptional.get();
        AuctionMongo auctionMongo = new AuctionMongo();
        auctionMongo.asignFrom(auctionOnly);
        productMongo.setAuction(auctionMongo);
      }
    }

    return CompletableFuture.completedFuture(productMongo);
  }

  @Async
  public CompletableFuture<AuctionResultRedis> getAuctionResultMongo(long id)
      throws NotFoundException {
    AuctionResultMongo data =
        auctionResultMDRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Auction result not found"));
    AuctionResultRedis productStatsRedis = beanMapper.auctionResultMongoToRedis(data);
    return CompletableFuture.completedFuture(productStatsRedis);
  }

  @Async
  public CompletableFuture<List<ProductImage>> getProductImageMongo(Long productId) {
    List<ProductImage> productImages1 = productImageMDRepository.findByProductId(productId);
    return CompletableFuture.completedFuture(productImages1);
  }
}
