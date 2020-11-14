package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.dtos.ProductCreateDTO;
import com.chozoi.product.app.dtos.UpdatePartialProductDTO;
import com.chozoi.product.app.dtos.VariantDTO;
import com.chozoi.product.domain.entities.mongodb.InventoryMongo;
import com.chozoi.product.domain.entities.mongodb.ProductMongo;
import com.chozoi.product.domain.entities.mongodb.ProductStatsMongo;
import com.chozoi.product.domain.entities.postgres.Auction;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.UserRoleState;
import com.chozoi.product.domain.entities.redis.AuctionResultRedis;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.repositories.postgres.ProductRepository;
import com.chozoi.product.domain.repositories.postgres.ProductVariantRepository;
import com.chozoi.product.domain.services.async.AsyncProductService;
import com.chozoi.product.domain.services.static_service.ProductStaticService;
import com.chozoi.product.domain.services.version_2.data.UpdateProductData;
import com.chozoi.product.domain.services.version_2.static_service.ProductStatic;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class Product2Service {

  @Autowired private ModelMapper modelMapper;

  @Autowired private Category2Service category2Service;
  @Autowired private ProductVariantService productVariantService;

  @Autowired private ImageService imageService;
  @Autowired private Shop2Service shop2Service;

  @Autowired private ProductVariantRepository productVariantRepository;

  @Autowired private TransportService transportService;

  @Autowired private ProductRepository repository;
  @Autowired private AuctionService auctionService;
  @Autowired private AsyncProductService asyncService;

  /** create product */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public Product create(Product product, UserRoleState userRoleState, boolean isPending)
      throws CloneNotSupportedException {
    // set state
    ProductStatic.setState(product, userRoleState, isPending);
    // handle image
    product.getImages().forEach(ProductImage::inferProperties);
    // inferProperties
    product.inferProperties();
    Auction auction = null;
    if (ProductStaticService.isAuction(product)) {
      auction = product.getAuction().clone();
      product.setAuction(null);
    }
    // save product
    repository.save(product);
    Product product1 = product.clone();
    if (ProductStaticService.isAuction(product)) {
      assert auction != null;
      auctionService.saveOnly(product1, auction);
    }
    ;
    return product1;
  }

  /**
   * check product is shop id
   *
   * @param productId
   * @param shopId
   * @return
   */
  public Product checkProductIsShop(Long productId, Integer shopId) throws Exception {
    Product product = repository.findByIdAndShopId(productId, shopId);
    if (Objects.isNull(product)) throw new NotFoundException("Product id: " + productId + " not found");
    return product;
  }

  /**
   * update product
   *
   * @param dto
   * @param productId
   * @param shopId
   * @param userRoleState
   * @return
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public UpdateProductData update(ProductCreateDTO dto, Long productId, Integer shopId, UserRoleState userRoleState)
      throws Exception {
    // mapper
    dto.setId(productId);
    Product productNew = modelMapper.dtoToProduct(dto);
    // check and get product
    Product product = checkProductIsShop(productId, shopId);
    // check state
    ProductStatic.checkStateUpdate(product);
    boolean isPending = product.getState() == ProductState.PENDING || dto.getIsPending();
    ProductStatic.setState(productNew, userRoleState, isPending);
    // clone
    Product productOld = product.clone();
    // category
    category2Service.setCategory(productNew);
    // attribute
    AttributeService.setAttribute(productNew);
    // transport
    transportService.checkTransport(productNew);
    // handle variant
    productVariantService.handleVariant(productOld, productNew);
    // handle image
    imageService.handleForUpdate(productOld, productNew);
    productNew.inferProperties();
    //
    Product productFull = productNew.clone();
    productFull.setShop(productOld.getShop());
    ProductStatic.mappingProduct(productOld, productNew);

    // save
    repository.save(productNew);
    // event update
    return UpdateProductData.builder().product(productFull).productOld(productOld).build();
  }

  /**
   * update partial
   *
   * @param dto
   * @param shopId
   * @param productId
   * @return
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public UpdateProductData updatePartial(
      UpdatePartialProductDTO dto, Integer shopId, Long productId) throws Exception {
    Product product = repository.findByIdAndShopId(productId, shopId);
    if (product == null) throw new Exception(ExceptionMessage.PRODUCT_NOT_FOUND);
    Product productOld = product.clone();
    ProductStatic.setDataCannotApproved(dto, product);
    // handle variant
    List<VariantDTO> variantDTOS = dto.getVariants();
    List<ProductVariant> variants = modelMapper.dtoToVariants(variantDTOS);
    product.setVariants(variants);
    product.inferProperties();
    productVariantService.handleVariant(productOld, product);
    // check transport
    transportService.checkTransport(product);
    // product validation
    ProductStatic.validation(product);
    // product new
    Product productNew = product.clone();
    // set null variant
    //    product.setVariants(new ArrayList<>());
    repository.save(product);
    return UpdateProductData.builder().productOld(productOld).product(productNew).build();
  }

  /**
   * get by id
   *
   * @param productId
   * @return
   */
  public ProductMongo getById(long productId) throws Exception {
    CompletableFuture<ProductMongo> product = asyncService.getProductMongo(productId);
    CompletableFuture<List<InventoryMongo>> inventory = asyncService.getInvetoryMongo(productId);
    CompletableFuture<ProductStatsMongo> stats = asyncService.getStatsMongo(productId);
    CompletableFuture<List<com.chozoi.product.domain.entities.mongodb.ProductImage>>
        productImageCompletableFuture = asyncService.getProductImageMongo(productId);
    CompletableFuture<AuctionResultRedis> resultCf = asyncService.getAuctionResult(productId);
    ProductMongo productMongo = product.get();
    List<InventoryMongo> inventories = inventory.get();
    ProductStatsMongo productStats = stats.get();
    List<com.chozoi.product.domain.entities.mongodb.ProductImage> images =
        productImageCompletableFuture.get();
    AuctionResultRedis result = resultCf.get();
    ProductMongo productMongo1 =
        ProductStatic.buildProductMongo(productMongo, inventories, productStats, images, result);
    if (!productMongo.getState().equals("PUBLIC")) throw new Exception("Sản phẩm id: " + productId + " không tồn tại");
    return productMongo1;
  }

  public Product getByIdRoleSeller(Long productId, Integer shopId) throws Exception {
    Product product = repository.findByIdAndShopId(productId, shopId);
    if (product == null) throw new Exception(ExceptionMessage.PRODUCT_NOT_FOUND);
    return product;
  }
}
