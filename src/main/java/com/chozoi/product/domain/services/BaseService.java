package com.chozoi.product.domain.services;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.responses.ChangeStateResponse;
import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.ProductLike;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.*;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
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
import com.chozoi.product.domain.services.design_patterns.change_state_product.ProductContext;
import com.chozoi.product.domain.services.design_patterns.change_state_product.StateFactory;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import com.chozoi.product.domain.services.design_patterns.change_state_product.state.StateProduct;
import com.chozoi.product.domain.utils.HandlerList;
import com.chozoi.product.domain.values.InventoryEventContent;
import com.chozoi.product.domain.values.ProductViewEventContent;
import com.chozoi.product.domain.values.content.InventoryLog;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Log4j2
class BaseService {
  @Autowired protected CategoryRepository categoryRepository;
  @Autowired protected HomeCategoryRepository categoryHomeRepository;
  @Autowired protected CategoryProductRepository categoryProductRepository;
  @Autowired protected ProductRepository productRepository;
  @Autowired protected ProductDraftRepository productDraftRepository;
  @Autowired protected AuctionPhaseRepository auctionPhaseRepository;

  @Autowired protected ProductVariantRepository productVariantRepository;
  @Autowired protected ShopRepository shopRepository;
  @Autowired protected AttributeRepository attributeRepository;
  @Autowired protected AttributeValueRepository attributeValueRepository;
  @Autowired protected ShippingSelectRepository shippingSelectRepository;
  @Autowired protected ShippingPartnerRepository shippingPartnerRepository;
  @Autowired protected PromotionRepository promotionRepository;
  @Autowired protected AuctionRepository auctionRepository;
  @Autowired protected ProductImageRepository productImageRepository;
  @Autowired protected InventoryRepository inventoryRepository;
  @Autowired protected InventoryKeepRepository inventoryKeepRepository;
  @Autowired protected InventoryHistoryRepository inventoryHistoryRepository;
  @Autowired protected DomainEventRepository domainEventRepository;
  @Autowired protected ProductReportIssueRepository productReportIssueRepository;
  @Autowired protected ProductStatsRepository productStatsRepository;
  @Autowired protected OrderRepository orderRepository;
  @Autowired protected CommentRepository commentRepository;
  @Autowired protected ModelMapper modelMapper;
  @Autowired protected CacheService cacheService;
  // redis
  @Autowired protected ProductRedisRepository productRedisRepository;
  @Autowired protected ProductImageRedisRepository productImageRedisRepository;
  @Autowired protected InventoryRedisRepository inventoryRedisRepository;
  @Autowired protected ProductLikeMDRepository productLikeMDRepository;
  protected List<ProductState> statesAcceptUpdate =
      Arrays.asList(
          ProductState.DRAFT,
          ProductState.REJECT,
          ProductState.PUBLIC,
          ProductState.READY,
          ProductState.PENDING);
  protected List<ProductState> statesAcceptDelete =
      Arrays.asList(
          ProductState.DRAFT, ProductState.REJECT, ProductState.READY, ProductState.PENDING);
  @Autowired protected StateFactory stateFactory;
  @Autowired protected ProductContext productContext;
  @Autowired EntityManager entityManager;
  @Autowired private BeanMapper beanMapper;
  @Autowired private DomainEventFactory domainEventFactory;
  @Autowired private DomainEventProducer domainEventProducer;
  @Autowired private NotificationFactory notificationFactory;;
  @Autowired private MailFactory mailFactory;

  protected void addStatusLikeProduct(List<ProductEs> products, String userId) {
    ProductLike productLike = productLikeMDRepository.findById(userId).orElse(null);
    List<Long> productIds =
        Objects.isNull(productLike) ? new ArrayList<>() : productLike.getProductIds();
    products.forEach(
        productEs -> {
          if (productIds.contains(productEs.getId())) productEs.setIsLiked(true);
          else productEs.setIsLiked(false);
        });
  }

  /**
   * check and get category
   *
   * @param id category
   * @return Category
   * @throws ResourceNotFoundException
   */
  protected Category getCategory(Integer id) throws ResourceNotFoundException {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ExceptionMessage.categoryNotFound(id)));
    if (category.getState() != CategoryState.PUBLIC)
      throw new ResourceNotFoundException(ExceptionMessage.categoryNotFound(id));
    return category;
  }

  protected List<Category> getTreeCategory(Integer id) throws ResourceNotFoundException {
    List<Category> categoryResponse = new ArrayList<>();
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ExceptionMessage.categoryNotFound(id)));
    categoryResponse.add(category);

    if (category.getLevel() != 3) {
      List<Integer> ids = new ArrayList<>();
      ids.add(id);
      List<Category> childs = getChild(ids);
      categoryResponse = HandlerList.merge(categoryResponse, childs);
      if (category.getLevel() == 1) {
        List<Integer> idsLevel2 = new ArrayList<>();
        for (Category child : childs) {
          idsLevel2.add(child.getId());
          List<Category> categoryLevel3 = getChild(idsLevel2);
          categoryResponse = HandlerList.merge(categoryResponse, categoryLevel3);
        }
      }
    }
    return categoryResponse;
  }

  private List<Category> getChild(List<Integer> ids) {
    Pageable pageable = PageRequest.of(0, 20000);
    return categoryProductRepository
        .findByStateAndParentIdIn(CategoryState.PUBLIC, ids, pageable)
        .getContent();
  }

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  protected List<Product> changeStateProducts(
      ProductState preState,
      ProductState newState,
      List<Long> ids,
      Integer shopId,
      Integer userSystemId,
      Integer userId,
      String reason)
      throws Exception {
    // check id user not null
    if (Objects.isNull(userId) && Objects.isNull(userSystemId)) throw new Exception(ExceptionMessage.USER_NOT_NULL);
    // get product draft
    List<ProductDraft> productList = productDraftRepository.findAllById(ids);
    // get product
    List<Product> products = productRepository.findAllById(ids);
    List<Product> productOlds = new ArrayList<>();
    for (Product product : products) productOlds.add(product.clone());

    if (Objects.nonNull(shopId)) {
      List<ProductDraft> productDraftErrors =
          productList.stream()
              .filter(v -> !v.getData().getShop().getId().equals(shopId))
              .collect(Collectors.toList());
      if (productDraftErrors.size() > 0) {
        List<Long> idserrors =
            productDraftErrors.stream().map(ProductDraft::getId).collect(Collectors.toList());
        throw new Exception(ExceptionMessage.ProductDraftNotFound(idserrors));
      }
    }
    List<ProductDraft> productDrafts =
        Objects.isNull(shopId)
            ? productList
            : productList.stream()
                .filter(v -> v.getData().getShop().getId().equals(shopId))
                .collect(Collectors.toList());
    // Factory Method Pattern
    StateProduct stateClass = stateFactory.getState(newState);
    // State Pattern
    productContext.setState(stateClass);
    productContext.changeState(productDrafts, products, preState);
    // even
    handleEvents(productOlds, products, userSystemId, userId, reason);
    return productDrafts.stream().map(ProductDraft::getData).collect(Collectors.toList());
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void updateDatabase(DataHandle data) {
    data.getProducts()
        .forEach(
            product -> {
              product.setVariants(null);
            });
    if (Objects.nonNull(data.getProducts())) productRepository.saveAll(data.getProducts());
    if (Objects.nonNull(data.getProductDrafts())) productDraftRepository.saveAll(data.getProductDrafts());
    if (Objects.nonNull(data.getAuctions())) auctionRepository.saveAll(data.getAuctions());
    if (Objects.nonNull(data.getImages())) productImageRepository.saveAll(data.getImages());
    if (Objects.nonNull(data.getVariants())) productVariantRepository.saveAll(data.getVariants());
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void handleEvents(
      List<Product> productOlds,
      List<Product> products,
      Integer userSystemId,
      Integer userId,
      String reason)
      throws Exception {
    for (Product productOld : productOlds) {
      List<Product> productList =
          products.stream()
              .filter(v -> v.getId().equals(productOld.getId()))
              .collect(Collectors.toList());
      if (productList.size() > 0) {
        Product product = productList.get(0);
        if (product.getState() != productOld.getState()) log.info("======== hanldeEvents");
        productStateChangedEvent(productOld, product, userSystemId, userId, reason);
      }
    }
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void saveProducts(List<Product> productsUpdate) {
    productsUpdate.forEach(
        product -> {
          product.setVariants(null);
        });
    productRepository.saveAll(productsUpdate);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void saveVariant(List<Product> productsUpdate) throws CloneNotSupportedException {
    List<ProductVariant> variants = new ArrayList<>();
    for (Product product : productsUpdate) {
      product.inferProperties();
      variants.addAll(product.getVariants());
    }
    productVariantRepository.saveAll(variants);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void deleteVariant(List<ProductVariant> variants) {
    variants.forEach(
        variant -> {
          productVariantRepository.deleteById(variant.getId());
          productVariantRepository.flush();
        });
  }

  private Product handleProduct(Long id, List<Product> products) {
    List<Product> productList =
        products.stream().filter(v -> v.getId().equals(id)).collect(Collectors.toList());
    return ObjectUtils.defaultIfNull(productList.get(0), new Product());
  }

  /**
   * Assign data response for change state
   *
   * @param state
   * @param products
   * @param ids
   * @return
   */
  protected ChangeStateResponse dataResponseToChangeState(
      ProductState state, List<Product> products, List<Long> ids) {
    List<Long> successIds = new ArrayList<>();

    for (Product product : products) if (product.getState() == state) successIds.add(product.getId());
    List<Long> errorIds = HandlerList.different(ids, successIds);
    return new ChangeStateResponse(successIds, errorIds);
  }

  /**
   * get and check Shop
   *
   * @param id shop
   * @return Shop
   * @throws ResourceNotFoundException
   */
  protected Shop getShop(Integer id) throws ResourceNotFoundException {
    // TODO : check state
    return shopRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.shopNotFound(id)));
  }

  /**
   * set check Shop
   *
   * @param shopId shop
   * @param product product
   */
  protected void setShop(int shopId, Product product) throws NotFoundException {
    Shop shop =
        shopRepository
            .findById(shopId)
            .orElseThrow(() -> new NotFoundException(ExceptionMessage.shopNotFound(shopId)));
    // TODO : check state
    product.setShop(shop);
  }

  /**
   * set Category
   *
   * @param product
   */
  void setCategory(Product product) throws NotFoundException {
    Integer categoryId = ObjectUtils.defaultIfNull(product.getCategory().getId(), 0);
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new NotFoundException(ExceptionMessage.categoryNotFound(categoryId)));
    product.setCategory(category);
  }

  // ==============================DOMAIN EVENT=========
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void productCreatedEvent(Product product, Integer userId) throws IOException {
    // save log
    DomainLogEvent domainLogEvent =
        domainEventFactory.createProductLog(product, EventType.ProductCreated, userId);
    domainEventRepository.save(domainLogEvent);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void productUpdatedEvent(Product preProduct, Product product, Integer userId)
      throws Exception {
    // save log
    DomainLogEvent domainLogEvent =
        domainEventFactory.createProductLog(product, EventType.ProductUpdated, userId);

    domainEventRepository.save(domainLogEvent);
  }

  /**
   * event change state for product
   *
   * @param product
   * @param userSystemId
   * @param userId
   * @throws IOException
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void productStateChangedEvent(
      Product preProduct, Product product, Integer userSystemId, Integer userId, String reason)
      throws Exception {
    log.error("================== " + preProduct.getState() + "=========> " + product.getState());
    // white log
    DomainLogEvent domainLogEvent =
        domainEventFactory.changeStateLog(
            product, preProduct.getState(), product.getState(), userSystemId, userId);
    domainEventRepository.save(domainLogEvent);

    // send notification
    if ((preProduct.getState().equals(ProductState.PENDING)
            && (product.getState().equals(ProductState.PUBLIC)
                || product.getState().equals(ProductState.READY)))
        || product.getState().equals(ProductState.REJECT)
        || product.getState().equals(ProductState.REJECTPRODUCT)
        || product.getState().equals(ProductState.REPORT)) {
      notificationFactory.sendEvent(
          product, String.valueOf(product.getState()), SegmentObjectType.PRODUCT, reason);
      mailFactory.sendEvent(
          product, String.valueOf(product.getState()), SegmentObjectType.PRODUCT, reason);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void productStateChangedEvent(
      Product product,
      ProductState preState,
      ProductState state,
      Integer userSystemId,
      Integer userId)
      throws IOException {
    // white log
    DomainLogEvent domainLogEvent =
        domainEventFactory.changeStateLog(product, preState, state, userSystemId, userId);
    domainEventRepository.save(domainLogEvent);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void productViewedEvent(Product product) {
    ProductViewEventContent eventContent = beanMapper.productToViewEventContent(product);
    DomainEvent event = domainEventFactory.productViewed(eventContent);
    domainEventProducer.save(event);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void inventoryCreatedEvent(Inventory inventory) {
    InventoryEventContent eventContent = beanMapper.inventoryToEventContent(inventory);
    DomainEvent event = domainEventFactory.inventoryCreated(eventContent);
    domainEventProducer.save(event);
  }

  // this
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected InventoryKeep inventoryQuantityChangedEvent(
      Inventory preInventory,
      Inventory inventory,
      InventoryHistoryState state,
      Product product,
      Integer userId,
      boolean saveKeep) {
    // save log
    Integer quantity = inventory.getInitialQuantity() + inventory.getInQuantity();
    Integer preQuantity = inventory.getInitialQuantity() + preInventory.getInQuantity();
    Integer remaining =
        inventory.getInitialQuantity() + inventory.getInQuantity() - inventory.getOutQuantity();
    Integer preRemaining =
        preInventory.getInitialQuantity()
            + preInventory.getInQuantity()
            - preInventory.getOutQuantity();
    InventoryLog log =
        InventoryLog.builder()
            .initialQuantity(inventory.getInitialQuantity())
            .preOutQuantity(preInventory.getOutQuantity())
            .outQuantity(inventory.getOutQuantity())
            .preQuantity(preQuantity)
            .quantity(quantity)
            .variantId(inventory.getId())
            .preRemainingQuantity(preRemaining)
            .remainingQuantity(remaining)
            .state(state)
            .build();
    InventoryKeep keep = new InventoryKeep();
    if (saveKeep) {
      DomainLogEvent domainLogEvent = domainEventFactory.changeQuantityLog(product, userId, log);
      domainEventRepository.save(domainLogEvent);
      Integer q = inventory.getOutQuantity() - preInventory.getOutQuantity();
      keep.setProductId(product.getId());
      keep.setVariantId(inventory.getId());
      keep.setDataLog(log);
      keep.setQuantity(q);
      inventoryKeepRepository.save(keep);
    }
    DomainLogEvent domainLogEvent = domainEventFactory.changeQuantityLog(product, userId, log);
    domainEventRepository.save(domainLogEvent);
    return keep;
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void priceChangedEvent(
      Long prePrice,
      Long price,
      Long preSalePrice,
      Long salePrice,
      Product product,
      Integer userId) {
    DomainLogEvent domainLogEvent =
        domainEventFactory.priceChangedLog(
            product, prePrice, price, preSalePrice, salePrice, userId);
    domainEventRepository.save(domainLogEvent);
  }

  /**
   * handle price for log
   *
   * @param productOld
   * @param product
   * @param userId
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void eventChangePrice(Product productOld, Product product, Integer userId) {
    List<ProductVariant> newVariants = product.getVariants();
    List<ProductVariant> oldVariants = productOld.getVariants();
    Map<Long, Long> mapSalePrice = new HashMap<>();
    Map<Long, Long> mapPrice = new HashMap<>();
    oldVariants.forEach(
        v -> {
          mapSalePrice.put(v.getId(), v.getSalePrice());
          mapPrice.put(v.getId(), v.getPrice());
        });
    newVariants.forEach(
        v -> {
          if (!Objects.isNull(mapSalePrice.get(v.getId()))) if (!mapSalePrice.get(v.getId()).equals(v.getSalePrice())
                  || !mapPrice.get(v.getId()).equals(v.getPrice())) {
            long preSalePrice = mapSalePrice.get(v.getId());
            long prePrice = mapPrice.get(v.getId());
            long price = Objects.isNull(v.getPrice()) ? 0 : v.getPrice();
            long salePrice = v.getSalePrice();
            priceChangedEvent(prePrice, price, preSalePrice, salePrice, product, userId);
          }
        });
  }

  /**
   * add inventory history for product
   *
   * @param product
   */
  protected void setInventoryHistory(Product product) {
    product
        .getVariants()
        .forEach(
            variant -> {
              if (variant.getId() == null) {
                List<InventoryHistory> histories = new ArrayList<>();
                InventoryHistory history = new InventoryHistory();
                history.assign(variant);
                histories.add(history);
                variant.setInventoryHistory(histories);
              }
            });
  }

  /**
   * hanlde and set state for product
   *
   * @param product
   * @param state
   * @param isPending
   */
  protected void setState(Product product, UserRoleState state, @NotNull Boolean isPending) {
    ProductState productState = product.getState();
    if (state == null) throw new ResourceNotFoundException(ExceptionMessage.NOT_SELLER);
    if (state == UserRoleState.REJECT) throw new ResourceNotFoundException(ExceptionMessage.USER_IS_REJECT);
    else if (state == UserRoleState.APPROVED) {
      if (product.getState() != ProductState.PUBLIC && product.getState() != ProductState.READY)
        productState = isPending ? ProductState.PENDING : ProductState.DRAFT;
    } else if (product.getState() != ProductState.PUBLIC
        && product.getState() != ProductState.READY) productState = ProductState.DRAFT;
    product.setState(productState);
  }

  /**
   * check and set attribute for product
   *
   * @param product
   */
  protected void setAttribute(Product product) throws NotFoundException {
    List<Integer> valueIds =
        product.getAttributes().stream()
            .map(AttributeProduct::getValue_id)
            .collect(Collectors.toList());
    Category category = product.getCategory();
    List<Attribute> attributes = category.getAttributes();
    List<Integer> attrRequiredIds =
        attributes.stream()
            .filter(Attribute::getIsRequired)
            .map(Attribute::getId)
            .collect(Collectors.toList());
    List<AttributeProduct> attributeProducts = new ArrayList<>();
    List<Integer> valueError = new ArrayList<>();
    Map<Integer, Integer> maps = new HashMap<>();
    Map<Integer, Attribute> attributeMap = new HashMap<>();
    Map<Integer, AttributeValue> valueMap = new HashMap<>();
    attributes.forEach(
        attribute -> {
          List<AttributeValue> values = attribute.getValues();
          attributeMap.put(attribute.getId(), attribute);
          values.forEach(
              v -> {
                maps.put(v.getId(), attribute.getId());
                valueMap.put(v.getId(), v);
              });
        });
    valueIds.forEach(
        id -> {
          if (Objects.isNull(maps.get(id))) valueError.add(id);
          else {
            int attributeId = maps.get(id);
            attrRequiredIds.removeIf(v -> v.equals(attributeId));
            AttributeProduct data =
                AttributeProduct.builder()
                    .id(attributeId)
                    .name(attributeMap.get(attributeId).getName())
                    .value_id(id)
                    .value(valueMap.get(id).getValue())
                    .build();
            attributeProducts.add(data);
          }
        });
    if (valueError.size() != 0) throw new NotFoundException(ExceptionMessage.attributeValueNotFound(valueError));
    // check attribute required
    if (attrRequiredIds.size() != 0) throw new NotFoundException(ExceptionMessage.attributeIsRequired(attrRequiredIds));
    product.setAttributes(attributeProducts);
  }

  /**
   * set new stat for product
   *
   * @param product
   */
  protected void setStats(Product product) {
    ProductStats stats = new ProductStats();
    product.setStats(stats);
  }

  /**
   * check shipping with packing size, weight and price
   *
   * @param product
   * @throws Exception
   */
  protected void checkTransport(Product product) throws Exception {
    List<ShippingPartner> shippingPartners = shippingPartnerRepository.findAll();
    List<ProductVariant> productVariant = product.getVariants();
    Integer[] size = product.getPackingSize();
    Integer weight = ObjectUtils.defaultIfNull(product.getWeight(), -1);
    AtomicInteger count_accept = new AtomicInteger(0);
    shippingPartners.forEach(
        shippingPartner -> {
          // check price
          AtomicInteger count_error = new AtomicInteger(0);
          productVariant.forEach(
              variant -> {
                if (variant.getSalePrice() > shippingPartner.getMaxValue()) count_error.incrementAndGet();
              });
          // check weight
          if (weight > shippingPartner.getMaxWeight()) count_error.incrementAndGet();
          // check packingsize
          Integer[] sizePartner = shippingPartner.getMaxSize();
          Arrays.sort(size);
          Arrays.sort(sizePartner);
          List<Integer> sizeList = Arrays.asList(size);
          List<Integer> sizePartnerList = Arrays.asList(sizePartner);
          for (int i = 0; i < size.length; i++)
            if (sizeList.get(i) > sizePartnerList.get(i)) count_error.incrementAndGet();

          if (count_error.get() == 0) count_accept.incrementAndGet();
        });
    if (count_accept.get() == 0) throw new Exception(
            ExceptionMessage.NO_MATCHING_SHIPPING_UNITS); // No matching shipping units found
  }

  /**
   * hanlde image for product - product update
   *
   * @param productNew
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void handleImage(Product productNew) {
    List<ProductImage> imagesUpdate = new ArrayList<>();
    // TODO: check image is product
    productNew
        .getImages()
        .forEach(
            image -> {
              if (image.getId() == null) image.setState(ProductImageState.PENDING);
              else
                image.setState(ProductImageState.PUBLIC);
              image.setProduct(productNew);
              image.setCreatedAt(LocalDateTime.now());
              image.setUpdatedAt(LocalDateTime.now());
              imagesUpdate.add(image);
            });
    productImageRepository.saveAll(imagesUpdate);
    productNew.setImages(imagesUpdate);
  }

  /**
   * handle variant product with update
   *
   * @param productOld
   * @param productNew
   * @return
   * @throws Exception
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void handleVariant(Product productOld, Product productNew) throws Exception {
    // handle variant
    List<ProductVariant> variants = productNew.getVariants();
    List<ProductVariant> variantsOld = productOld.getVariants();
    List<ProductVariant> variantUpdate = new ArrayList<>();
    if (Objects.isNull(variants)) throw new Exception("variants not null");
    variants.forEach(
        variant -> {
          variant.setState(VariantState.PUBLIC);
          variant.getInventory().setOutQuantity(0);
          if (Objects.nonNull(variant.getId())) {
            List<ProductVariant> variantList =
                variantsOld.stream()
                    .filter(v -> v.getId().equals(variant.getId()))
                    .collect(Collectors.toList());
            if (variantList.size() > 0) {
              variant
                  .getInventory()
                  .setOutQuantity(variantList.get(0).getInventory().getOutQuantity());
              variantsOld.remove(variantList.get(0));
            } else variants.remove(variant);
          }
        });
    variantsOld.forEach(
        variantOld -> {
          variantOld.setState(VariantState.DELETED);
        });
    variants.addAll(variantsOld);
    variants.forEach(ProductVariant::inferProperties);
    productNew.setVariants(variants);
  }
}
