package com.chozoi.product.domain.services.version_2;

import com.bussinesslog.analytics.domain.models.DeviceType;
import com.bussinesslog.analytics.domain.models.LogMessage;
import com.bussinesslog.analytics.domain.models.ProductInfo;
import com.bussinesslog.analytics.domain.models.ProductType;
import com.chozoi.product.app.dtos.ProductBuilderDTO;
import com.chozoi.product.app.dtos.ProductCreateDTO;
import com.chozoi.product.app.dtos.RestartAuctionDTO;
import com.chozoi.product.app.responses.ProductResponse;
import com.chozoi.product.data.response.ProductDataResponse;
import com.chozoi.product.domain.entities.mongodb.ProductImage;
import com.chozoi.product.domain.entities.mongodb.ProductLike;
import com.chozoi.product.domain.entities.mongodb.ProductMongo;
import com.chozoi.product.domain.entities.mongodb.ShopStats;
import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductActiveCode;
import com.chozoi.product.domain.entities.postgres.types.ProductCondition;
import com.chozoi.product.domain.entities.postgres.types.UserRoleState;
import com.chozoi.product.domain.entities.redis.ShopStatsRedis;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.factories.BeanMapper;
import com.chozoi.product.domain.producers.AuctionProducer;
import com.chozoi.product.domain.producers.ProductLogProducer;
import com.chozoi.product.domain.repositories.mongodb.ProductLikeMDRepository;
import com.chozoi.product.domain.repositories.mongodb.ShopStatsMDRepository;
import com.chozoi.product.domain.repositories.postgres.CategoryRepository;
import com.chozoi.product.domain.repositories.postgres.ProductActiveCodeRepository;
import com.chozoi.product.domain.repositories.postgres.ProductRepository;
import com.chozoi.product.domain.repositories.redis.ShopStatsRedisRepository;
import com.chozoi.product.domain.services.mongo.BuyerProductService;
import com.chozoi.product.domain.services.version_2.data.UpdateProductData;
import com.chozoi.product.domain.services.version_2.services.AttributeService;
import com.chozoi.product.domain.utils.ProductUtils;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import message.product.accept.Value;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class ProductFacade extends BaseFacade {

  @Autowired private BuyerProductService buyerProductService;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductLikeMDRepository productLikeMDRepository;
  @Autowired private AuctionProducer auctionProducer;
  @Autowired private BeanMapper beanMapper;
  @Autowired private ShopStatsRedisRepository shopStatsRedisRepository;
  @Autowired private ShopStatsMDRepository shopStatsMDRepository;
  @Autowired private ProductLogProducer productLogProducer;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductActiveCodeRepository productActiveCodeRepository;

  /**
   * create new product
   *
   * @param productDto
   * @param userRoleState
   * @param userId
   * @param shopId
   * @return
   * @throws Exception
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Product create(
      ProductCreateDTO productDto, UserRoleState userRoleState, int userId, int shopId)
      throws Exception {
    // shop
    Product product = modelMapper.dtoToProduct(productDto);
    // set shop
    shop2Service.setShop(product, shopId, userId);
    // set category
    categoryService.setCategory(product);
    // check attribute
    AttributeService.setAttribute(product);
    // create stats
    productStatService.setStats(product);
    // check transport
    transportService.checkTransport(product);
    // save product
    product = product2Service.create(product, userRoleState, productDto.getIsPending());
    // product draft
    productDraftService.create(product);
    // event product
    productEvent.productCreatedEvent(product, userId);
    // log new product
    logNewProduct(product, userId, shopId);
    // clean cache
    return product;
  }

  private void logNewProduct(Product product, int userId, int shopId) {
    LogMessage logMessage = new LogMessage();
    logMessage.setEventType("new_product");
    logMessage.setUserId(String.valueOf(userId));
    logMessage.setDeviceId("");
    logMessage.setShopId(shopId);
    logMessage.setDeviceType(DeviceType.UNKNOWN);
    logMessage.setBrowser("");
    logMessage.setOsName("");
    logMessage.setOsVersion("");
    logMessage.setIp("");
    logMessage.setCity("");
    logMessage.setCountry("");
    logMessage.setTime(product.getCreatedAt().toString());

    ProductInfo productInfo = new ProductInfo();
    productInfo.setProductId(String.valueOf(product.getId()));
    ProductType productType = ProductType.NORMAL;
    if (ProductUtils.AUCTION_TYPE.contains(product.getType())) {
      productType = ProductType.BID;
    } else if (product.getCondition().equals(ProductCondition.USED)) {
      productType = ProductType.OLD;
    }
    productInfo.setProductType(productType);
    if (product.getCategory() != null) {
      productInfo.setProductCategory(product.getCategory().getName());
      if (product.getCategory().getLevel() > 1) {
        Category category =
                categoryRepository
                        .findById(product.getCategory().getParentId())
                        .orElse(null);
        if (category != null) {
          productInfo.setProductCategory(category.getName());
          if (category.getLevel() > 1) {
            Category root =
                    categoryRepository
                            .findById(category.getParentId())
                            .orElse(null);
            if (root != null) {
              productInfo.setProductCategory(root.getName());
            }
          }
        }
      }

    }
    logMessage.setProductInfo(productInfo);

    productLogProducer.save(logMessage);
  }
  // TODO: tungdx
  //  /**
  //   * update partial for product
  //   *
  //   * @return
  //   */
  //  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  //  public boolean changeStateAuction(
  //      Integer shopId, Long productId, @Valid ChangeStateAuctionDto dto) throws Exception {
  //    Product product = product2Service.getByIdRoleSeller(productId, shopId);
  //    auctionService.changeState(product.getAuction(), dto.getState());
  //    return true;
  //  }

  /**
   * udpate product
   *
   * @param productId
   * @param shopId
   * @param dto
   * @param userRoleState
   * @param userId
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Product update(
      Long productId, Integer shopId, ProductCreateDTO dto, UserRoleState userRoleState, Integer userId)
      throws Exception {
    // product service
    UpdateProductData data = product2Service.update(dto, productId, shopId, userRoleState);
    Product product = data.getProduct();
    Product productOld = data.getProductOld();
    // product draft
    productDraftService.update(dto, product);
    // event update
    productEvent.productUpdatedEvent(product, userId);
    // event changeState
    productEvent.productStateChangedEvent(productOld, product, null, userId, null);
    // event change price
    productEvent.eventChangePrice(productOld, product, userId);
    // TODO: clean cache
    return product;
  }

  /**
   * get product for buyer
   *
   * @param productId
   * @param userId
   * @return
   */
  public ProductResponse getProductForBuyer(long productId, String userId) throws Exception {
    userId = ObjectUtils.defaultIfNull(userId, "guest");

    ProductMongo product = product2Service.getById(productId);
    // like product
    ProductLike productLike = productLikeMDRepository.findById(userId).orElse(new ProductLike());
    List<Long> likeIds = ObjectUtils.defaultIfNull(productLike.getProductIds(), new ArrayList<>());
    boolean liked = likeIds.contains(productId);
    //
    if (Objects.nonNull(product.getImages())) product.getImages().sort(Comparator.comparing(ProductImage::getSort));
    ModelMapper mapper = new ModelMapper();
    ProductDataResponse productResponse = mapper.map(product, (Type) ProductDataResponse.class);
    productResponse.setIsLiked(liked);
    boolean isPrivate = false;
    if (product.getPrivateCode() != null && !product.getPrivateCode().isEmpty()) {
      isPrivate = true;
    }
    productResponse.setIsPrivate(isPrivate);

    boolean privateActive = false;
    try {
      Integer uId = Integer.parseInt(userId);
      ProductActiveCode productActiveCode = productActiveCodeRepository.findFirstByUserIdAndProductId(uId, (int) productId);
      if (productActiveCode != null) {
        privateActive = true;
      }
    } catch (NumberFormatException nfe) {

    }
    productResponse.setPrivateActive(privateActive);

    productResponse.inferProperties();

    //shop start
    ShopStats shopStats = getStats(product.getShop().getId());
    productResponse.getShop().setStats(shopStats);
    return new ProductResponse<>(productResponse);
  }

  public ShopStats getStats(int shopId) {
    ShopStatsRedis shopStats = shopStatsRedisRepository.findById(shopId).orElse(null);
    if (shopStats == null) {
      ShopStats stats = shopStatsMDRepository.findById(shopId).orElse(new ShopStats());
      shopStatsRedisRepository.save(beanMapper.shopStatsMongoToRedis(stats));
      return stats;
    } else {
      return beanMapper.shopStatsRedisToMongo(shopStats);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Product reAuction(Long productId, Integer shopId, Integer userId, RestartAuctionDTO dto)
      throws Exception {
    Product product = product2Service.getByIdRoleSeller(productId, shopId);
    if (!ProductUtils.isAuction(product.getType())) return null;
    Product product1 = product.clone();
    ProductCreateDTO productDTO = ProductBuilderDTO.builder(product1, dto);
    Product product2 = create(productDTO, UserRoleState.APPROVED, userId, shopId);
    //
    acceptAuction(product2);
    return product2;
  }

  private void acceptAuction(Product product2) throws Exception {
    Value value = new Value();
    value.setId(product2.getId());
    log.info("============ accept product id : " + product2.getId());
    auctionProducer.save(value);
  }
}
