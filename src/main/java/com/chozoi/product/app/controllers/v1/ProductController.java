package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.dtos.ProductCategoryDTO;
import com.chozoi.product.app.responses.BlogHomeResponse;
import com.chozoi.product.app.responses.ConfigHomeResponse;
import com.chozoi.product.app.responses.ProductsEsResponse;
import com.chozoi.product.app.responses.VariantResponse;
import com.chozoi.product.data.response.ProductRelatedResponse;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.data.response.home.HomeData;
import com.chozoi.product.domain.entities.elasticsearch.AuctionResultEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.services.BlogService;
import com.chozoi.product.domain.services.CacheService;
import com.chozoi.product.domain.services.SuggestionService;
import com.chozoi.product.domain.services.elasticsearch.HomeService;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController()
@RequestMapping("/v1")
@Log4j2
public class ProductController extends BaseProductController {

  @Autowired protected CacheService cacheService;

  @Autowired protected HomeService homeService;
  @Autowired protected BlogService blogService;

  @Autowired protected SuggestionService suggestionService;

  // =================================== PUBLIC ====================================

  @Value("${environment}")
  private String cluster;

  /**
   * get not rated
   *
   * @param userId
   * @param pageable
   * @return
   */
  @GetMapping(
      path = "/products",
      params = {"collection=NotYetRated"})
  public ProductsEsResponse notYetRated(
      @RequestHeader("X-Chozoi-User-Id") Integer userId, Pageable pageable) {
    Page<ProductEs> page = productService.notYetRated(userId, pageable);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  /**
   * get config for home
   *
   * @param size
   * @param userId
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @GetMapping(
      path = "/products",
      params = {"collection=config", "for=home"})
  public List<ConfigHomeResponse> configProduct(
      @RequestParam("size") List<Integer> size, @RequestParam("userId") String userId)
      throws Exception {
    List<ConfigHomeResponse> responses = new ArrayList<>();

    return responses;
  }

  /**
   * get config for home
   *
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @GetMapping(
      path = "/products",
      params = {"collection=blogs", "for=home"})
  public List<BlogHomeResponse> blogForHome() throws Exception {
    return blogService.getForHome();
  }

  /**
   * get config for home
   *
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @GetMapping(
      path = "/products",
      params = {"collection=config_v2", "for=home"})
  public List<HomeData> config2Product(String userId) throws Exception {
    return homeService.homeConfig(userId);
  }

  /**
   * get config for home
   *
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @GetMapping(
      path = "/products/{id}",
      params = {"collection=config_v2", "for=home"})
  public List<ProductsPublicResponse> configProductByTask(
      String userId, @PathVariable(value = "id") Integer configId, @RequestParam Integer taskId)
      throws Exception {
    return homeService.findByTaskHome(userId, configId, taskId);
  }

  //  /**
  //   * Get product by id
  //   *
  //   * @param productId
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(path = "/products/{productId}")
  //  public ProductResponse getProduct(@PathVariable long productId, String userId) throws
  // Exception {
  //    Map<String, Object> map = productMongoService.getById(productId, userId);
  //    ProductMongo product = (ProductMongo) map.get("product");
  //    if (Objects.nonNull(product.getImages()))
  // product.getImages().sort(Comparator.comparing(ProductImage::getSort));
  //    ModelMapper mapper = new ModelMapper();
  //    ProductDataResponse productResponse = mapper.map(product, (Type) ProductDataResponse.class);
  //    productResponse.setIsLiked((Boolean) map.get("liked"));
  //    productResponse.inferProperties();
  //    return new ProductResponse<>(productResponse);
  //  }

  /**
   * Get product by id
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(path = "/products")
  public List<ProductsPublicResponse> getProducts(
      @RequestParam(value = "ids") String ids, Integer shopId) throws Exception {
    return productService.getAllProductById(ids, shopId);
  }

  /**
   * Get product by id
   *
   * @param
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/products",
      params = {"collection=variant"})
  public List<VariantResponse> getVariant(@RequestParam("ids") String data)
      throws InterruptedException, ExecutionException, NotFoundException {
    return productMongoService.getVariant(data);
  }

  /**
   * Get products by category
   *
   * @param pageable
   * @param categoryId
   * @return response
   */
  @GetMapping(
      path = "/products",
      params = {"collection=category"})
  public ProductsEsResponse getProductByCategory(
      Pageable pageable,
      @RequestParam("category_id") Integer categoryId,
      @RequestParam("type") String type,
      @RequestParam("valueIds") String valueIds,
      @RequestParam("sort") String sort)
      throws ResourceNotFoundException, IOException {
    List<String> result = new ArrayList<>();
    if (!valueIds.equals("")) result = Arrays.asList(valueIds.substring(1, valueIds.length() - 1).split(","));

    List<Integer> ids = new ArrayList<>();
    if (result.size() != 0) result.forEach(
            id -> {
                if (!id.equals("")) ids.add(Integer.valueOf(id));
            });
    ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
    productCategoryDTO.setType(type);
    productCategoryDTO.setSort(sort);
    productCategoryDTO.setValueIds(ids);
    Page<ProductEs> page =
        productElasticService.getByCategory(categoryId, productCategoryDTO, pageable);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  /**
   * Get new products for home
   *
   * @param pageable
   * @return response
   */
  // TODO : change to suggestion
  @GetMapping(
      path = "/products",
      params = {"collection=new", "for=home"})
  public ProductsEsResponse getProductSuggestion(String userId, Pageable pageable)
      throws Exception {
    userId = Objects.isNull(userId) ? String.valueOf(UUID.randomUUID()) : userId;
    Page<ProductEs> page = homeService.suggestion(userId, pageable);
    ProductsEsResponse response = new ProductsEsResponse<>(page, ProductsPublicResponse.class);
    if (cluster.equals("PROD")) {
      response.getMetadata().setTotalPages(10);
      response.getMetadata().setPage(pageable.getPageNumber());
      response.getMetadata().setSize(pageable.getPageSize());
    }
    return response;
  }

  /**
   * Get auction products for home
   *
   * @param pageable
   * @return response
   */
  @GetMapping(
      path = "/products",
      params = {"collection=auction", "for=home"})
  public ProductsEsResponse getHomeAuction(Pageable pageable) {
    Page<ProductEs> products = productElasticService.getHomeAuction(pageable);
    products
        .getContent()
        .forEach(
            productEs -> {
              try {
                if (Objects.isNull(productEs.getAuction().getResult())) {
                  AuctionResultEs resultEs = new AuctionResultEs();
                  productEs.getAuction().setResult(resultEs);
                }
              } catch (Exception e) {
                log.info(productEs.getId());
              }
            });
    return new ProductsEsResponse<>(products, ProductsPublicResponse.class);
  }

  /**
   * Get promotion products for home
   *
   * @param pageable
   * @return
   */
  @GetMapping(
      path = "/products",
      params = {"collection=promotion", "for=home"})
  public ProductsEsResponse getHomePromotion(
      @PageableDefault(page = 0, size = 20) Pageable pageable) {
    return new ProductsEsResponse<>(
        productElasticService.getHomePromotion(pageable), ProductsPublicResponse.class);
  }

  /**
   * Get promotion products for home
   *
   * @param pageable
   * @return
   */
  @GetMapping(
      path = "/products/{configId}",
      params = {"collection=config", "for=home"})
  public List<ProductEs> loadMore(
      @PathVariable(value = "configId") Integer configId,
      String userId,
      @PageableDefault(page = 0, size = 20) Pageable pageable) {
    ProductsEsResponse productsEsResponse =
        new ProductsEsResponse<>(
            homeService.suggestion(userId, pageable), ProductsPublicResponse.class);
    return productsEsResponse.getProducts();
  }

  /**
   * Get related products for a specific product
   *
   * @param productId
   * @param pageable
   * @return response
   */
  @GetMapping(
      value = "/products",
      params = {"collection=related"})
  public ProductsEsResponse getProductRelation(
      @RequestParam("product_id") long productId, Pageable pageable) throws Exception {
    return new ProductsEsResponse<>(
        productElasticService.getProductRelation(productId, pageable),
        ProductRelatedResponse.class);
  }

  /**
   * Get recommended products for a specific product
   *
   * @param productId
   * @param pageable
   * @return response
   */
  @GetMapping(
      value = "/products",
      params = {"collection=recommended"})
  public ProductsEsResponse getProductInterest(
      @RequestParam("product_id") long productId, @PageableDefault(size = 6) Pageable pageable)
      throws Exception {
    return new ProductsEsResponse<>(
        productElasticService.getProductInterest(productId, pageable),
        ProductsPublicResponse.class);
  }

  /**
   * filter product
   *
   * @param pageable
   * @return response
   */
  @GetMapping(
      value = "/products",
      params = {"collection=filter"})
  public ProductsEsResponse filter(
      String type,
      String categoryIds,
      Long minPrice,
      Long maxPrice,
      Integer rating,
      Boolean freeShip,
      String condition,
      String provinces,
      Integer shopId,
      String sortField,
      String sort,
      String userId,
      @PageableDefault() Pageable pageable)
      throws ResourceNotFoundException {
    Page<ProductEs> page =
        productElasticService.filter(
            type,
            categoryIds,
            shopId,
            minPrice,
            maxPrice,
            rating,
            freeShip,
            condition,
            provinces,
            sortField,
            sort,
            userId,
            pageable);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  /**
   * Get recommended products for a specific product
   *
   * @param pageable
   * @return response
   */
  @GetMapping(
      value = "/products",
      params = {"collection=bestSeller", "for=home"})
  public ProductsEsResponse bestSellerForHome(@PageableDefault(size = 18) Pageable pageable) {
    Page<ProductEs> page = productElasticService.bestSellerForHome(pageable);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }
}
