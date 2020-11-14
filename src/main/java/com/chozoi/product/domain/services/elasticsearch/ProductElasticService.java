package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.app.dtos.ProductCategoryDTO;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.redis.HomeProduct;
import com.chozoi.product.domain.utils.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
@Log4j2
public class ProductElasticService extends BaseElasticService {

  public Page<ProductEs> getByCategory(
          Integer categoryId, ProductCategoryDTO productCategoryDTO, Pageable pageable) {
    List<String> type = this.getType(productCategoryDTO.getType());
    List<Integer> attributes = productCategoryDTO.getValueIds();
    SortOrder sortOrder =
            productCategoryDTO.getSort().equals("ASC") ? SortOrder.ASC : SortOrder.DESC;
    SortBuilder<FieldSortBuilder> sortBuilder = SortBuilders.fieldSort("price").order(sortOrder);
    QueryBuilder queryBuilder = this.getQueryBuilder(categoryId, type, attributes);
    SearchQuery searchQuery =
            new NativeSearchQueryBuilder()
                    .withQuery(queryBuilder)
                    .withIndices(this.productIndex())
                    .withTypes(this.productType())
                    .withPageable(pageable)
                    .withSort(sortBuilder)
                    .build();

    return productRepository.search(searchQuery);
  }

  /**
   * get product new for home
   *
   * @param pageable
   * @return
   */
  public Page<ProductEs> getProductNew(Pageable pageable) {
    List<String> type = this.getType("NORMAL");
    Page<ProductEs> productEs = this.getData(type, pageable);
    return productEs;
  }

  private void saveRedisHome(String key, Page<ProductEs> productEs) {
    ModelMapper modelMapper = new ModelMapper();
    List<ProductsPublicResponse> productListResponses = new ArrayList<>();
    productEs
            .getContent()
            .forEach(
                    v -> {
                      String str =
                              v.getDescription().length() > 200
                                      ? v.getDescription().substring(0, 200)
                                      : v.getDescription();
                      v.setDescription(str);
                      productListResponses.add(modelMapper.map(v, ProductsPublicResponse.class));
                    });
    homeRedisRepository.save(new HomeProduct(key, productListResponses));
  }

  public Page<ProductEs> getHomeAuction(Pageable pageable) {
    //    String key = "auction";
    //    HomeProduct productAuction =
    // homeRedisRepository.findById(key).orElse(HomeProduct.builder().build());
    //    if ( Objects.isNull(productAuction.getCriterian()) ) {
    List<String> type = this.getType("AUCTION");
    Page<ProductEs> productEs = this.getData(type, pageable);
    //    this.saveRedisHome(key, productEs);
    return productEs;
    //    } else {
    //      List<ProductEs> productEs =
    // modelMapper.productsResponseToEs(productAuction.getProducts());
    //      return new PageImpl<>(productEs, pageable, productEs.size());
    //    }
  }

  public Page<ProductEs> getHomePromotion(Pageable pageable) {
    //    String key = "promotion";
    //    HomeProduct productPromotion =
    // homeRedisRepository.findById(key).orElse(HomeProduct.builder().build());
    //    if ( Objects.isNull(productPromotion.getCriterian()) ) {
    List<String> type = this.getType("PROMOTION");
    Page<ProductEs> productEs = this.getData(type, pageable);
    //      this.saveRedisHome(key, productEs);
    return productEs;
    //    } else {
    //      List<ProductEs> productEs =
    // modelMapper.productsResponseToEs(productPromotion.getProducts());
    //      return new PageImpl<>(productEs, pageable, productEs.size());
    //    }
  }

  /**
   * filter product
   *
   * @param type
   * @param minPrice
   * @param maxPrice
   * @param rating
   * @param freeShip
   * @param condition
   * @param sortField
   * @param sort
   * @param pageable
   * @return
   */
  public Page<ProductEs> filter(
          String type,
          String categoryIds,
          Integer shopId,
          Long minPrice,
          Long maxPrice,
          Integer rating,
          Boolean freeShip,
          String condition,
          String provinces,
          String sortField,
          String sort,
          String userId,
          Pageable pageable) {
    List<Integer> cateIds = new ArrayList<>();
    userId = ObjectUtils.defaultIfNull(userId, "guest");
    String[] cateStrIds = new String[0];
    if (Objects.nonNull(categoryIds)) cateStrIds = categoryIds.split(",");
    List<String> list = Arrays.asList(cateStrIds);
    list.forEach(
            l -> {
              try {
                Integer id = Integer.valueOf(l);
                cateIds.add(id);
              } catch (Exception e) {
              }
            });
    Long minPriceFilter = ObjectUtils.defaultIfNull(minPrice, 0L);
    Long maxPriceFilter = maxPrice == null ? 1000000000 : maxPrice;
    SortOrder sortOrder = sort == null || sort.equals("DESC") ? SortOrder.DESC : SortOrder.ASC;
    String sortFields = sortField == null ? "id" : sortField;
    Double ratingFilter = rating == null || rating < 0 || rating > 5 ? -0.0009 : rating;
    SortBuilder<FieldSortBuilder> sortBuilder = SortBuilders.fieldSort(sortFields).order(sortOrder);
    List<String> types;
    if (Objects.isNull(type)) types = ProductUtils.PRODUCT_ALL_TYPE_STR;
    else if (type.equals("ALL")) types = ProductUtils.PRODUCT_ALL_TYPE_STR;
    else if (type.equals("AUCTION") || type.equals("AUCTION_BIDDING")) types = ProductUtils.AUCTION_TYPE_STR;
    else
      types = ProductUtils.PRODUCT_TYPE_STR;
    BoolQueryBuilder queryBuilder =
            boolQuery()
                    .filter(termQuery("state", "PUBLIC"))
                    .must(rangeQuery("salePrice").from(minPriceFilter).to(maxPriceFilter))
                    .filter(termsQuery("type", types))
                    .mustNot(termQuery("shop.isLock", true))
                    .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
                    .must(rangeQuery("stats.averageRating").from(ratingFilter).to(5.1));
    queryBuilder.mustNot(termQuery("auction.state", "STOPPED"));
    queryBuilder.mustNot(termQuery("auction.state", "WAITING"));
    queryBuilder.mustNot(rangeQuery("auction.timeEnd").lte(System.currentTimeMillis()));
    if (!list.isEmpty()) queryBuilder.filter(termsQuery("categories.id", list));
    if (condition != null && (condition.equals("NEW") || condition.equals("USED")))
      queryBuilder.filter(termQuery("condition.keyword", condition));
    if (cateIds.size() > 0) queryBuilder.filter(termsQuery("categories.id", cateIds));
    if (shopId != null) queryBuilder.filter(termQuery("shop.id", shopId));
    if (freeShip != null) {
      queryBuilder.filter(termQuery("freeShipStatus", freeShip));
    }
    if (provinces != null) {
      List<String> arrays = Arrays.asList(provinces.split(",", 100));
      List<Integer> provincesId = new ArrayList<>();
      arrays.forEach(
              a -> {
                provincesId.add(Integer.valueOf(a));
              });

      queryBuilder.filter(termsQuery("shop.provinces.id", provincesId));
    }
    if (Objects.nonNull(type))
      if (type.equals("AUCTION_BIDDING")) queryBuilder.filter(termQuery("auction.state", "BIDING"));
    SearchQuery searchQuery =
            new NativeSearchQueryBuilder()
                    .withQuery(queryBuilder)
                    .withIndices(this.productIndex())
                    .withTypes(this.productType())
                    .withPageable(pageable)
                    .withSort(sortBuilder)
                    .build();
    Page<ProductEs> products = productRepository.search(searchQuery);
    this.addStatusLikeProduct(products.getContent(), userId);
    return products;
  }

  public Page<ProductEs> getProductRelation(long productId, Pageable pageable) throws Exception {
    ProductEs product =
            productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found"));
    Integer categoryId = product.getCategory().getId();
    SortBuilder<FieldSortBuilder> sortBuilder =
            SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    QueryBuilder queryBuilder =
            boolQuery()
                    .mustNot(termQuery("shop.isLock", true))
                    .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
                    .filter(termQuery("categories.id", categoryId))
                    .mustNot(termQuery("id", productId))
                    .filter(termQuery("state", "PUBLIC"));
    SearchQuery searchQuery =
            new NativeSearchQueryBuilder()
                    .withQuery(queryBuilder)
                    .withIndices(this.productIndex())
                    .withTypes(this.productType())
                    .withPageable(pageable)
                    .withSort(sortBuilder)
                    .build();
    return productRepository.search(searchQuery);
  }

  public Page<ProductEs> bestSellerForHome(Pageable pageable) {
    //    String key = "bestSeller_" + pageable.getPageNumber();
    //    HomeProduct products =
    // homeRedisRepository.findById(key).orElse(HomeProduct.builder().build());
    //    if ( Objects.isNull(products.getCriterian()) ) {
    SortBuilder<FieldSortBuilder> sortBuilder =
            SortBuilders.fieldSort("soldQuantity").order(SortOrder.DESC);
    QueryBuilder queryBuilder = boolQuery().filter(termQuery("state", "PUBLIC"))
            .mustNot(termQuery("shop.isLock", true))
            .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)));
    SearchQuery searchQuery =
            new NativeSearchQueryBuilder()
                    .withQuery(queryBuilder)
                    .withIndices(this.productIndex())
                    .withTypes(this.productType())
                    .withPageable(pageable)
                    .withSort(sortBuilder)
                    .build();
    Page<ProductEs> productEs = productRepository.search(searchQuery);
    //      this.saveRedisHome(key, productEs);
    return productEs;
    //    } else {
    //      List<ProductEs> productEs = modelMapper.productsResponseToEs(products.getProducts());
    //      return new PageImpl<>(productEs, pageable, productEs.size());
    //    }
  }

  public Page<ProductEs> getProductInterest(long productId, Pageable pageable) throws Exception {
    ProductEs product =
            productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found"));
    Long shopId = product.getShop().getId();
    SortBuilder<FieldSortBuilder> sortBuilder =
            SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    QueryBuilder queryBuilder =
            boolQuery()
                    .mustNot(termQuery("shop.isLock", true))
                    .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
                    .filter(termQuery("shop.id", shopId))
                    .mustNot(termQuery("id", productId))
                    .filter(termQuery("state", "PUBLIC"));
    SearchQuery searchQuery =
            new NativeSearchQueryBuilder()
                    .withQuery(queryBuilder)
                    .withIndices(this.productIndex())
                    .withTypes(this.productType())
                    .withPageable(pageable)
                    .withSort(sortBuilder)
                    .build();

    return productRepository.search(searchQuery);
  }
}
