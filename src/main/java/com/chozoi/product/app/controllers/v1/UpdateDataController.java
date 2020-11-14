package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.elasticsearch.ShopEs;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import com.chozoi.product.domain.factories.DomainEventFactory;
import com.chozoi.product.domain.producers.factories.NotificationFactory;
import com.chozoi.product.domain.repositories.elasticsearch.ProductDraftEsRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ShopEsRepository;
import com.chozoi.product.domain.repositories.postgres.*;
import com.chozoi.product.domain.repositories.redis.AuctionResultRedisRepository;
import com.chozoi.product.domain.repositories.redis.InventoryRedisRepository;
import com.chozoi.product.domain.repositories.redis.ProductRedisRepository;
import com.chozoi.product.domain.services.design_patterns.caching.DataChain;
import com.chozoi.product.domain.services.design_patterns.database_factory.DatabaseFactory;
import com.chozoi.product.domain.services.mongo.BuyerProductService;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;

@RestController()
@RequestMapping("/dev")
@Log4j2
public class UpdateDataController {

  @Autowired protected InventoryRepository inventoryRepository;
  @Autowired protected ProductVariantRepository productVariantRepository;

  @Autowired protected ProductRedisRepository productRedisRepository;

  @Autowired protected InventoryRedisRepository inventoryRedisRepository;

  @Autowired protected ProductRepository productRepository;
  @Autowired protected ProductDraftRepository productDraftRepository;

  @Autowired protected ProductDraftEsRepository productDraftEsRepository;
  @Autowired protected ShopEsRepository shopEsRepository;

  @Autowired protected ProductEsRepository productEsRepository;

  @Autowired protected AuctionResultRedisRepository auctionResultRedisRepository;
  @Autowired protected ModelMapper modelMapper;
  @Autowired DomainEventFactory domainEventFactory;
  @Autowired DatabaseFactory databaseFactory;
  @Autowired private DataChain multidatase;
  @Autowired private MongoOperations mongoOps;
  @Autowired private DomainEventRepository domainEventRepository;
  @Autowired private BuyerProductService buyerProductService;
  @Autowired private ConfigLayoutBlockRepository configLayoutBlockRepository;
  @Autowired private NotificationFactory notificationFactory;
  @Autowired private DataChain dataChain;

  @GetMapping(path = "/test")
  public Page<ProductEs> update() throws Exception {
    BoolQueryBuilder queryBuilder = boolQuery().mustNot(existsQuery("shop.provinces"));
    PageRequest pageRequest = new PageRequest(0, 50);
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("chozoi_products")
            .withPageable(pageRequest)
            .withTypes("_doc")
            .build();
    Page<ProductEs> productEs = productEsRepository.search(searchQuery);
    List<ProductEs> productEsList = productEs.getContent();
    for (ProductEs productEs1 : productEsList) {
      try {
        productEs1.getShop().getId();
      } catch (Exception e) {
        throw new Exception(String.valueOf(productEs1.getId()));
      }
      ShopEs shopEs =
          shopEsRepository
              .findById(Math.toIntExact(productEs1.getShop().getId()))
              .orElseThrow(
                  () -> new Exception("Shop id : " + productEs1.getShop().getId() + " not found"));
      productEs1.getShop().setProvinces(shopEs.getProvinces());
    }
    productEsRepository.saveAll(productEsList);
    ;
    return productEs;
  }

  @GetMapping(path = "/productConfig")
  public List productConfig() throws Exception {
    return dataChain.getData().next(LayoutBlock.class);
  }

  @GetMapping(path = "/productEs/{id}")
  public ProductEs productEs(@PathVariable(value = "id") Long id) throws Exception {
    return productEsRepository.findById(id).get();
  }
}
