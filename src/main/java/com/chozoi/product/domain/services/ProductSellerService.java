package com.chozoi.product.domain.services;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.dtos.*;
import com.chozoi.product.app.responses.ChangeStateResponse;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.*;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSellerService extends BaseService {

  @Autowired private ModelMapper modelMapper;

  @Autowired private CacheService cacheService;

  private List<ProductType> typesForSeller =
      new ArrayList<>(
          Arrays.asList(
              ProductType.CLASSIFIER,
              ProductType.PROMOTION,
              ProductType.NORMAL,
              ProductType.SPECIAL));
  private List<ProductType> typesAuctionForSeller =
      new ArrayList<>(Arrays.asList(ProductType.AUCTION_SALE, ProductType.AUCTION));
  private List<ProductState> statesForSeller =
      new ArrayList<>(
          Arrays.asList(
              ProductState.PUBLIC,
              ProductState.PENDING,
              ProductState.READY,
              ProductState.DRAFT,
              ProductState.REJECT,
              ProductState.REPORT));

  /**
   * create product
   *
   * @return exception
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Product store(
      Product product,
      UserRoleState userRoleState,
      int userId,
      int shopId,
      @NotNull Boolean isPending)
      throws Exception {
    // set state
    setState(product, userRoleState, isPending);
    // shop
    setShop(shopId, product);
    // check category
    setCategory(product);
    // check attribute
    setAttribute(product);
    // check transport
    checkTransport(product);
    // create history inventory
    setInventoryHistory(product);
    // create stats
    setStats(product);
    // handle image
    product.getImages().forEach(ProductImage::inferProperties);
    // inferProperties
    product.inferProperties();
    // event inventory
    product.getVariants().stream()
        .peek(
            (productVariant -> {
              inventoryCreatedEvent(productVariant.getInventory());
            }));
    // event product
    Product productSave = productRepository.save(product);
    ProductDraft productDraft =
        ProductDraft.builder()
            .id(productSave.getId())
            .data(productSave)
            .state(productSave.getState())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .updatedVersion(1)
            .build();
    productDraftRepository.save(productDraft);
    productCreatedEvent(product, userId);
    // save product
    return product;
  }

  /**
   * update product
   *
   * @param productId
   * @param shopId
   * @param productInput
   * @param isPending
   * @param stateSeller
   * @param userId
   * @return
   * @throws Exception
   */
  @Transactional(
      propagation = Propagation.REQUIRED,
      rollbackFor = Exception.class,
      isolation = Isolation.SERIALIZABLE)
  public Product update(
      Long productId,
      Integer shopId,
      Product productInput,
      boolean isPending,
      UserRoleState stateSeller,
      Integer userId)
      throws Exception {
    // find product by shopId and productId
    Product product = productRepository.findByIdAndShopId(productId, shopId);
    ProductDraft productDrafts =
        productDraftRepository
            .findById(productId)
            .orElseThrow(() -> new Exception("product id : " + productId + " not found"));
    if (Objects.isNull(product)) throw new NotFoundException("Product id: " + productId + " not found");
    if (!statesAcceptUpdate.contains(product.getState()))
        throw new NotFoundException("state " + product.getState() + " not update");
    Product productOld = product.clone();
    productOld.setState(productDrafts.getState());
    productInput.setId(productId);
    productInput.setShop(product.getShop());
    productInput.setStats(product.getStats());
    // set state
    productInput.setState(product.getState());
    // check category
    setCategory(productInput);
    // check attribute
    setAttribute(productInput);
    // check transport
    checkTransport(productInput);
    // create history inventory
    setInventoryHistory(productInput);

    productInput.setCreatedAt(product.getCreatedAt());
    // write log
    eventChangePrice(productOld, productInput, userId);
    //        // TODO:: check quantity
    //        //    productQuantityChanged(product, productUpdate, userId);
    productUpdatedEvent(productOld, productInput, userId);
    ProductDraft productDraftOld =
        productDraftRepository.findById(productId).orElse(new ProductDraft());
    Integer version =
        Objects.isNull(productDraftOld.getId())
                || Objects.isNull(productDraftOld.getUpdatedVersion())
            ? 1
            : productDraftOld.getUpdatedVersion();
    version += 1;
    ProductDraft productDraft = new ProductDraft();
    productDraft.setId(productId);
    productDraft.setData(productInput);
    productDraft.setState(ProductState.PENDING);
    productDraft.setUpdatedVersion(version);
    productDraftRepository.save(productDraft);
    handleVariant(productOld, productInput);
    handleImage(productInput);
    productInput.inferProperties();
    List<Product> products = new ArrayList<>(Collections.singletonList(productInput));
    saveVariant(products);
    mappingProduct(productOld, productInput);
    saveProducts(products);
    eventChangeState(productOld, productInput, userId);
    cacheService.cleanAllProductCache(productId);
    return productInput;
  }

  private void eventChangeState(Product productOld, Product productInput, Integer userId)
      throws Exception {
    this.productStateChangedEvent(productOld, productInput, null, userId, null);
  }

  private void mappingProduct(Product productOld, Product productInput) {
    productInput.setId(productOld.getId());
    productInput.setVariants(null);
    productInput.setImages(productOld.getImages());
    productInput.setAttributes(productOld.getAttributes());
    productInput.setCategory(productOld.getCategory());
    productInput.setCategories(productOld.getCategories());
    productInput.setClassifiers(productOld.getClassifiers());
    productInput.setPackingSize(productInput.getPackingSize());
    productInput.setAutoPublic(productOld.getAutoPublic());
    productInput.setAuction(productOld.getAuction());
    productInput.setWeight(productInput.getWeight());
    productInput.setCondition(productOld.getCondition());
    productInput.setDescription(productOld.getDescription());
    productInput.setDescriptionPinking(productOld.getDescriptionPinking());
    productInput.setDescriptionPinkingIn(productOld.getDescriptionPinkingIn());
    productInput.setDescriptionPinkingOut(productOld.getDescriptionPinkingOut());
    productInput.setIsQuantityLimited(productInput.getIsQuantityLimited());
    productInput.setName(productOld.getName());
    productInput.setShippingPartnerIds(productOld.getShippingPartnerIds());
    productInput.setType(productOld.getType());
    productInput.setVideos(productOld.getVideos());
    productInput.setCreatedAt(productOld.getCreatedAt());
  }

  /**
   * Update product partial
   *
   * @param dto
   * @param shopId
   * @param productId
   * @param userId
   * @return
   * @throws NotFoundException
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public boolean updatePartial(
      UpdatePartialProductDTO dto, Integer shopId, Long productId, Integer userId)
      throws Exception {
    ProductDraft productDraft =
        productDraftRepository
            .findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));
    Product product1 =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));
    Product product = productDraft.getData();
    if (!product.getShop().getId().equals(shopId)) throw new NotFoundException("Product not is shop");
    List<ProductVariant> productVariants =
        productVariantRepository.findByProduct_IdAndState(productId, VariantState.PUBLIC);
    Product productOld = product.clone();
    if (Objects.nonNull(dto.getIsQuantityLimited())) product.setIsQuantityLimited(dto.getIsQuantityLimited());
    productOld.setVariants(productVariants);
    product.setPackingSize(dto.getPackingSize());
    product.setWeight(dto.getWeight());
    product.setFreeShipStatus(dto.getFreeShipStatus());
    if (Objects.nonNull(dto.getClassifiers())) product.setClassifiers(dto.getClassifiers());
    List<VariantDTO> variantDTOS = dto.getVariants();
    List<ProductVariant> variants = modelMapper.dtoToVariants(variantDTOS);
    product.setVariants(variants);
    // check transport
    checkTransport(product);
    handleVariant(productOld, product);

    //
    product.inferProperties();
    if (product.getType() == ProductType.CLASSIFIER) {
      if (product.getVariants().size() < 2) throw new Exception("variant failed");
    } else if (product.getVariants().size() > 1) throw new Exception("variant failed");
    List<ProductVariant> variantList = new ArrayList<>();
    for (ProductVariant variant : product.getVariants()) variantList.add(variant.clone());
    productVariantRepository.saveAll(product.getVariants());
    // update variant in draft product
    productDraft.setData(product);
    product.setVariants(null);
    productDraft.getData().setVariants(variantList);
    productDraftRepository.save(productDraft);
    product.setState(product1.getState());
    productRepository.save(product);
    // TODO : update partial event;
    cacheService.cleanAllProductCache(productId);
    return true;
  }

  /**
   * change state
   *
   * @param userId
   * @param shopId
   * @param ids
   * @param preState
   * @param state
   * @return
   * @throws Exception
   */
  public ChangeStateResponse changeStateForProducts(
      Integer userId, Integer shopId, List<Long> ids, ProductState preState, ProductState state)
      throws Exception {
    List<Product> products = changeStateProducts(preState, state, ids, shopId, null, userId, null);
    List<Long> successIds = products.stream().map(Product::getId).collect(Collectors.toList());
    return new ChangeStateResponse(successIds, new ArrayList<>());
  }

  /**
   * Get products by shop role seller
   *
   * @param shopId
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  public Page<Product> getByShopSeller(Integer shopId, Pageable pageable)
      throws ResourceNotFoundException {
    Shop shop = getShop(shopId);
    return productRepository.findByShopAndTypeInAndStateInOrderByIdDesc(
        shop, typesForSeller, statesForSeller, pageable);
  }

  /**
   * Get product auction role seller
   *
   * @param shopId
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  public Page<Product> getShopAuction(Integer shopId, Pageable pageable)
      throws ResourceNotFoundException {
    Shop shop = getShop(shopId);
    return productRepository.findByShopAndTypeInAndStateInOrderByIdDesc(
        shop, typesAuctionForSeller, statesForSeller, pageable);
  }

  /**
   * Add quantity for varriant
   *
   * @param dto
   * @param shopId
   * @param userId
   * @return
   * @throws Exception
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Boolean addQuantity(ChangeQuantityDTO dto, Integer shopId, Integer userId)
      throws Exception {
    Long productId = dto.getProductId();
    Long variantId = dto.getVariantId();
    Integer quantity = dto.getQuantity();
    // get product
    Product product =
        productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found"));
    if (!product.getShop().getId().equals(shopId)) throw new ResourceNotFoundException(
            "Product id: " + productId + " not found in shop id: " + shopId);

    // update inventory
    List<ProductVariant> productVariants =
        product.getVariants().stream()
            .filter(variant -> variant.getId().equals(variantId))
            .collect(Collectors.toList());
    ProductVariant variant = productVariants.get(0);
    Inventory inventory = variant.getInventory();
    Inventory preInventory = inventory.clone();
    InventoryHistoryState state;
    inventory.setInQuantity(quantity + inventory.getOutQuantity());
    inventoryRepository.save(inventory);

    // update inventory history
    InventoryHistory inventoryHistory = new InventoryHistory();
    inventoryHistory.setVariant(variant);
    inventoryHistory.setQuantity(quantity);
    inventoryHistory.setType(InventoryHistoryState.ADDED);
    inventoryHistoryRepository.save(inventoryHistory);
    // log
    inventoryQuantityChangedEvent(
        preInventory, inventory, InventoryHistoryState.ADDED, product, userId, false);
    // clearCache
    cacheService.cleanAllProductCache(productId);
    return true;
  }

  /**
   * get product role seller
   *
   * @param productId
   * @param shopId
   * @return
   * @throws Exception
   */
  public Product productRoleSeller(Long productId, Integer shopId) throws Exception {
    ProductDraft productDraft =
        productDraftRepository.findById(productId).orElse(null); // getProductReal
    if (Objects.isNull(productDraft)) productDraft = getProductReal(productId);
    Product product = productDraft.getData();
    List<ProductVariant> variantList = productVariantRepository.findByProductId(productId);
    product.setVariants(variantList);
    checkShop(product, shopId);
    product.setState(productDraft.getState());
    setCategories(product);
    // sort variant
    if (Objects.isNull(product.getVariants()) || product.getVariants().isEmpty()) {
      List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
      variants.sort(Comparator.comparingLong(ProductVariant::getId));
      product.setVariants(variants);
    } else product.getVariants().sort(Comparator.comparingLong(ProductVariant::getId));
    // sort images
    List<ProductImage> images = new ArrayList<>();
    if (productDraft.getState().equals(ProductState.PUBLIC)
        || productDraft.getState().equals(ProductState.READY))
        images = productImageRepository.findByProductIdAndState(productId, ProductImageState.PUBLIC);
    else images =
            productImageRepository.findByProductIdAndStateIn(
                    productId, Arrays.asList(ProductImageState.PUBLIC, ProductImageState.PENDING));

    if (Objects.nonNull(images)) images.forEach(
            productImage -> {
                Integer sort = Objects.isNull(productImage.getSort()) ? 0 : productImage.getSort();
                productImage.setSort(sort);
                if (Objects.isNull(productImage.getId())) product.getImages().remove(productImage);
            });
    images.sort(Comparator.comparing(ProductImage::getSort));
    product.setImages(images);
    return product;
  }

  private void setCategories(Product product) {
    List<Category> categories = this.getCategoriesFromRoot(product.getCategory());
    categories.forEach(
        category -> {
          category.setAttributes(null);
        });
    product.setCategories(categories);
  }

  private void checkShop(Product product, Integer shopId) {
    if (!product.getShop().getId().equals(shopId))
        throw new ResourceNotFoundException("Product not is shop id: " + shopId);
  }

  private ProductDraft getProductReal(Long productId) throws Exception {
    Product product =
        productRepository.findById(productId).orElseThrow(() -> new Exception("product not found"));
    ProductDraft productDraft =
        ProductDraft.builder().data(product).id(productId).state(product.getState()).build();
    productDraftRepository.save(productDraft);
    return productDraft;
  }

  /**
   * get category from root
   *
   * @param leafCategory
   * @return
   */
  private List<Category> getCategoriesFromRoot(Category leafCategory) {

    List<Category> categories = new ArrayList<>();
    categories.add(leafCategory);

    Integer parentId = leafCategory.getParentId();

    while (parentId != null) {
      Optional<Category> optionalCategory = categoryRepository.findById(parentId);

      if (optionalCategory.isPresent()) {
        Category category = optionalCategory.get();
        categories.add(category);
        parentId = category.getParentId();
      } else parentId = null;
    }

    categories.sort(Comparator.comparingInt((Category::getLevel)));

    return categories;
  }

  /**
   * create image for product
   *
   * @param dto
   * @param shopId
   * @return
   * @throws Exception
   */
  public ProductImage createImage(CreateImageDto dto, Integer shopId) throws Exception {
    String url = dto.getImageUrl();
    Long productId = dto.getProductId();
    Product product = productRepository.findByIdAndShopId(productId, shopId);
    if (product == null) throw new Exception("Product not found");
    ProductImage productImage = new ProductImage();
    productImage.setProduct(product);
    productImage.setImageUrl(url);
    productImage.setState(ProductImageState.PUBLIC);
    productImage.setSort(dto.getSort());
    return productImageRepository.save(productImage);
  }

  /**
   * delete image
   *
   * @param dto
   * @param shopId
   * @param productId
   * @return
   * @throws Exception
   */
  public ChangeStateResponse deleteImage(DeleteImageDto dto, Integer shopId, Long productId)
      throws Exception {
    List<Long> ids = dto.getIds();
    List<ProductImage> productImages = productImageRepository.findAllById(ids);
    Product product =
        productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found"));
    if (!product.getShop().getId().equals(shopId))
        throw new Exception("Product id:" + productId + " not is shop id: " + shopId);
    List<Long> idSucces = new ArrayList<>();
    List<Long> iderrors = new ArrayList<>();
    productImages.forEach(
        img -> {
          if (!img.getProduct().getId().equals(productId)) {
            iderrors.add(img.getId());
            productImages.remove(img);
          } else idSucces.add(img.getId());
        });
    try {
      productImageRepository.deleteAll(productImages);
    } catch (Exception ignored) {
      throw new Exception("Update database fail");
    }

    return new ChangeStateResponse(idSucces, iderrors);
  }

  public ProductState checkState(Long id) {
    ProductDraft product =
        productDraftRepository
            .findById(id)
            .orElseThrow(() -> new ExpressionException("product not found"));
    return product.getState();
  }
}
