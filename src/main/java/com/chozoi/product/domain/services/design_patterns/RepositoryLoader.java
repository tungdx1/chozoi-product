package com.chozoi.product.domain.services.design_patterns;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.factories.BeanMapper;
import com.chozoi.product.domain.factories.DomainEventFactory;
import com.chozoi.product.domain.producers.DomainEventProducer;
import com.chozoi.product.domain.producers.factories.MailFactory;
import com.chozoi.product.domain.producers.factories.NotificationFactory;
import com.chozoi.product.domain.repositories.mongodb.ProductLikeMDRepository;
import com.chozoi.product.domain.repositories.postgres.*;
import com.chozoi.product.domain.repositories.redis.InventoryRedisRepository;
import com.chozoi.product.domain.repositories.redis.ProductImageRedisRepository;
import com.chozoi.product.domain.repositories.redis.ProductRedisRepository;
import com.chozoi.product.domain.services.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

@Service
@ComponentScan(basePackages = {"com.chozoi.product.domain.repositories.redis"})
public abstract class RepositoryLoader {
  @Autowired
  public CategoryRepository categoryRepository;
  @Autowired
  public HomeCategoryRepository categoryHomeRepository;
  @Autowired
  public CategoryProductRepository categoryProductRepository;
  @Autowired
  public ProductRepository productRepository;
  @Autowired
  public ProductDraftRepository productDraftRepository;

  @Autowired
  public ProductVariantRepository productVariantRepository;
  @Autowired
  public ShopRepository shopRepository;
  @Autowired
  public AttributeRepository attributeRepository;
  @Autowired
  public AttributeValueRepository attributeValueRepository;
  @Autowired
  public ShippingSelectRepository shippingSelectRepository;
  @Autowired
  public ShippingPartnerRepository shippingPartnerRepository;
  @Autowired
  public PromotionRepository promotionRepository;
  @Autowired
  public AuctionRepository auctionRepository;
  @Autowired
  public ProductImageRepository productImageRepository;
  @Autowired
  public InventoryRepository inventoryRepository;
  @Autowired
  public InventoryKeepRepository inventoryKeepRepository;
  @Autowired
  public InventoryHistoryRepository inventoryHistoryRepository;
  @Autowired
  public DomainEventRepository domainEventRepository;
  @Autowired
  public ProductReportIssueRepository productReportIssueRepository;
  @Autowired
  public ProductStatsRepository productStatsRepository;
  @Autowired
  public OrderRepository orderRepository;
  @Autowired
  public CommentRepository commentRepository;
  @Autowired
  public ModelMapper modelMapper;
  @Autowired
  public CacheService cacheService;
  // redis
  @Autowired
  public ProductRedisRepository productRedisRepository;
  @Autowired
  public ProductImageRedisRepository productImageRedisRepository;
  @Autowired
  public InventoryRedisRepository inventoryRedisRepository;
  @Autowired
  public ProductLikeMDRepository productLikeMDRepository;
  public List<ProductState> statesAcceptUpdate =
          Arrays.asList(
                  ProductState.DRAFT,
                  ProductState.REJECT,
                  ProductState.PUBLIC,
                  ProductState.READY,
                  ProductState.PENDING);
  public List<ProductState> statesAcceptDelete =
          Arrays.asList(
                  ProductState.DRAFT, ProductState.REJECT, ProductState.READY, ProductState.PENDING);
  @Autowired
  public BeanMapper beanMapper;
  @Autowired
  public DomainEventFactory domainEventFactory;
  @Autowired
  public DomainEventProducer domainEventProducer;
  @Autowired
  public NotificationFactory notificationFactory;
  @Autowired
  public MailFactory mailFactory;
  @Autowired
  EntityManager entityManager;
}
