package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.app.dtos.elasticsearch.BucketDTO;
import com.chozoi.product.data.elasticsearch.AggregationName;
import com.chozoi.product.domain.entities.elasticsearch.CategoriesEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductDraftEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.services.elasticsearch.static_service.VariableStatic;
import com.chozoi.product.domain.utils.JsonParser;
import com.chozoi.product.domain.utils.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

@Component
@Log4j2
public class ShopProductService extends BaseElasticService {

  /**
   * Get product sold off in shop - public
   *
   * @param shopId
   * @param pageable
   * @param params
   * @return
   */
  public Page<ProductEs> getProductSoldOutByShop(
      int shopId, Pageable pageable, Map<String, String> params) {
    BoolQueryBuilder queryBuilder =
        getShopQueryBuilder(shopId)
            .filter(termQuery("isQuantityLimited", true))
            .must(QueryBuilders.rangeQuery("remainingQuantity").lte(0));
    SortBuilder sortPrice = sortBuilder(params);

    SortBuilder sortBuilder = SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices(this.productIndex())
            .withTypes(this.productType())
            .withSort(sortPrice)
            .withSort(sortBuilder)
            .withPageable(pageable)
            .build();
    return shopProductRepository.search(searchQuery);
  }

  public Page<ProductEs> getProductAuctionByShop(int shopId, Pageable pageable) {
    BoolQueryBuilder queryBuilder =
        getShopQueryBuilder(shopId).filter(termQuery("type", "AUCTION"));
    SortBuilder sortBuilder = SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("chozoi_products")
            .withTypes("_doc")
            .withSort(sortBuilder)
            .withPageable(pageable)
            .build();
    return shopProductRepository.search(searchQuery);
  }

  public List<CategoriesEs> getShopCategories(int shopId) throws IOException {
    List<BucketDTO> shopCategoriesBuckets = getCategoriesIds(shopId);
    List<Integer> categoriesIds =
        shopCategoriesBuckets.stream().map(BucketDTO::getKey).collect(Collectors.toList());
    // TODO: count doc
    if (categoriesIds.size() == 0) throw new ResourceNotFoundException("Categories not found !");
    return categoriesRepository.findCategoriesByStateAndIdInOrderBySortAsc("PUBLIC", categoriesIds);
  }

  public List<CategoriesEs> getShopCategoriesLevel1(int shopId) throws IOException {
    List<BucketDTO> shopCategoriesBuckets = getCategoriesIds(shopId);
    List<Integer> categoriesIds =
        shopCategoriesBuckets.stream().map(BucketDTO::getKey).collect(Collectors.toList());
    if (categoriesIds.size() == 0) throw new ResourceNotFoundException("Categories not found !");
    return categoriesRepository.findCategoriesByStateAndIdInOrderBySortAsc("PUBLIC", categoriesIds);
  }

  public Page<ProductEs> getProductCategoriesByShop(
      int shopId, int categoriesId, Pageable pageable, Map<String, String> params) {

    SortBuilder sortPrice = sortBuilder(params);
    List<String> types = getTypes(params);
    String name = getTermField(params);

    List<String> notIn = new ArrayList<>();
    notIn.add("AUCTION");
    notIn.add("AUCTION_SALE");

    BoolQueryBuilder queryBuilder =
        getShopQueryBuilder(shopId)
            .filter(termQuery("categories.id", categoriesId))
            .filter(termsQuery(name, types))
            .mustNot(QueryBuilders.termsQuery("type", notIn));

    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("chozoi_products")
            .withTypes("_doc")
            .withSort(sortPrice)
            .withPageable(pageable)
            .build();
    return shopProductRepository.search(searchQuery);
  }

  public Page<ProductEs> getProductAllByShop(
      int shopId, Pageable pageable, Map<String, String> params) {

    SortBuilder sortPrice = sortBuilder(params);
    List<String> types = getTypes(params);
    String name = getTermField(params);

    List<String> notIn = new ArrayList<>();
    notIn.add("AUCTION");
    notIn.add("AUCTION_SALE");

    BoolQueryBuilder queryBuilder =
        getShopQueryBuilder(shopId)
            .filter(termsQuery(name, types))
            .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
            .mustNot(QueryBuilders.termsQuery("type", notIn));

    SortBuilder sortBuilder = SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("chozoi_products")
            .withTypes("_doc")
            .withSort(sortPrice)
            .withSort(sortBuilder)
            .withPageable(pageable)
            .build();
    return shopProductRepository.search(searchQuery);
  }

  private SortBuilder sortBuilder(Map<String, String> params) {
    SortBuilder sortPrince = null;
    if (params.size() > 0) if (params.get("orderBy") != null) sortPrince = sortBuilder(params.get("orderBy"));
    else
      sortPrince = SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    else
      sortPrince = SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    return sortPrince;
  }

  private List<String> getTypes(Map<String, String> params) {
    List<String> types = new ArrayList<>();

    if (params.size() > 0)
      if (params.get("type") != null) if (params.get("type").equals("auction")) types = getType("AUCTION");
      else if (params.get("type").equals("normal")) types = getType("NORMAL");
      else types.add("PUBLIC");
      else types.add("PUBLIC");
    else types.add("PUBLIC");
    return types;
  }

  private String getTermField(Map<String, String> params) {
    String name = null;
    if (params.size() > 0) if (params.get("type") != null) if (params.get("type").equals("auction")) name = "type";
    else if (params.get("type").equals("normal")) name = "type";
    else name = "state";
    else name = "state";
    else name = "state";
    return name;
  }

  private BoolQueryBuilder getShopQueryBuilder(int shopId) {
    return QueryBuilders.boolQuery()
        .filter(termQuery("shop.id", shopId))
        .filter(termQuery("state", "PUBLIC"));
  }

  private List<BucketDTO> getCategoriesIds(int shopId) throws IOException {
    QueryBuilder queryBuilder =
        QueryBuilders.boolQuery()
            .filter(termQuery("shop.id", shopId))
            .filter(termsQuery("state", "PUBLIC"));
    //            .filter(termQuery("categories.level", 3));
    //            .filter(termQuery("categories.level", 3));
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices(this.productIndex())
            .withTypes(this.productType())
            .addAggregation(
                terms("categories")
                    .field("category.id")
                    .size(100)
                    .shardSize(20)
                    .showTermDocCountError(true))
            .build();

    Aggregations aggregations =
        elasticsearchTemplate.query(
            searchQuery,
            new ResultsExtractor<Aggregations>() {
              @Override
              public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
              }
            });
    String json = JsonParser.toJson(aggregations.get("categories"));
    AggregationName aggregationName = JsonParser.entity(json, AggregationName.class);
    return modelMapper.aggregationToBucketDTO(aggregationName.getBuckets());
  }

  public Page<ProductDraftEs> search(
      Map<String, String> params, Integer shopId, Pageable pageable) {
    String keyWord = params.get("key_word");
    String aspect = params.get("aspect"); // aspect,
    String type = params.get("type") == null ? "ALL" : params.get("type");
    String auction = params.get("auction");
    List<String> state = new ArrayList<>();
    state.add(params.get("state"));
    List<String> states = params.get("state") == null ? this.productState() : state;
    List<String> types = ProductUtils.productType(type);
    BoolQueryBuilder queryBuilder =
        boolQuery()
            .filter(termsQuery("state", states))
            .filter(termQuery("shop.id", shopId))
            .filter(termsQuery("type", types));
    if (auction != null) {
      List<String> auctions = new ArrayList<>();
      if (auction.equals("AUCTION")) {
        auctions = Arrays.asList("AUCTION_SALE", "AUCTION");
      } else if (auction.equals("AUCTION_FLASH_BID")) {
        auctions = Collections.singletonList("AUCTION_FLASH_BID");
      }

      if (auctions.size() > 0) {
        queryBuilder.filter(termsQuery("type", auctions));
      }
    }
    QueryBuilderService.searchName(queryBuilder, keyWord);
    QueryBuilderService.soldOff(queryBuilder, aspect);
    QueryBuilderService.auctionState(queryBuilder, params.get("auction_state"));
    // building
    SearchQuery searchQuery =
        QueryBuilderService.builder(queryBuilder, VariableStatic.PRODUCT_DRAFT_INDEX, pageable);

    return productDraftEsRepository.search(searchQuery);
  }

  /**
   * product for coupon
   *
   * @param shopId
   * @param name
   * @param categoryIds
   * @param pageable
   * @return
   */
  public Page<ProductEs> forCoupon(
      Integer shopId, String name, String categoryIds, Pageable pageable) {
    BoolQueryBuilder queryBuilder = boolQuery().must(termQuery("shop.id", shopId));
    List<Integer> cateIds = regexCategory(categoryIds);
    queryBuilder.must(termQuery("state", "PUBLIC"));
    List<String> typeNormal = Arrays.asList("NORMAL", "CLASSIFIER");
    if (name != null) queryBuilder.must(matchQuery("name", name));
    if (!cateIds.isEmpty()) queryBuilder.filter(termsQuery("categories.id", cateIds));
    queryBuilder.must(
        boolQuery()
            .should(boolQuery().must(termQuery("auction.state", "BIDING")))
            .should(boolQuery().must(termsQuery("type", typeNormal))));
    SortBuilder sortBuilder = SortBuilders.fieldSort("id").order(SortOrder.DESC);
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("chozoi_products")
            .withTypes("_doc")
            .withSort(sortBuilder)
            .withPageable(pageable)
            .build();
    return productRepository.search(searchQuery);
  }

  private List<Integer> regexCategory(String categoryId) {
    //
    List<Integer> cateIds = new ArrayList<>();
    String[] cateStrIds = new String[0];
    if (Objects.nonNull(categoryId)) cateStrIds = categoryId.split(",");
    List<String> list = Arrays.asList(cateStrIds);
    list.forEach(
        l -> {
          try {
            Integer id = Integer.valueOf(l);
            cateIds.add(id);
          } catch (Exception e) {
          }
        });
    return cateIds;
  }
}
