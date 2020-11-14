package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.data.response.home.HomeData;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.ProductLike;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockGroupMongo;
import com.chozoi.product.domain.entities.mongodb.config_home.ProductGroup;
import com.chozoi.product.domain.entities.mongodb.config_home.ProductGroupMongo;
import com.chozoi.product.domain.repositories.elasticsearch.CategoriesRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProductDraftEsRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.mongodb.ProductLikeMDRepository;
import com.chozoi.product.domain.repositories.mongodb.ShopMDRepository;
import com.chozoi.product.domain.repositories.redis.HomeRedisRepository;
import com.chozoi.product.domain.services.SuggestionService;
import com.chozoi.product.domain.utils.ProductUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class BaseElasticService {
  @Autowired protected ProductEsRepository productRepository;
  @Autowired protected CategoriesRepository categoriesRepository;
  @Autowired protected ModelMapper modelMapper;
  @Autowired protected ElasticsearchTemplate elasticsearchTemplate;
  @Autowired protected ProductEsRepository shopProductRepository;
  @Autowired protected ShopMDRepository shopMDRepository;

  @Autowired protected ProductDraftEsRepository productDraftEsRepository;
  @Autowired protected ProductLikeMDRepository productLikeMDRepository;

  // redis
  @Autowired protected HomeRedisRepository homeRedisRepository;

  @Value("${ELASTICSEARCH_TRANSPORT_HOST}")
  protected String elasticUrl;

  @Autowired private SuggestionService suggestionService;

  protected final String productIndex() {
    return "chozoi_products";
  }

  protected final String productType() {
    return "_doc";
  }

  protected final List<String> productState() {
    List<String> stateList = new ArrayList<>();
    stateList.add("PUBLIC");
    stateList.add("DRAFT");
    stateList.add("PENDING");
    stateList.add("READY");
    stateList.add("REJECT");
    return stateList;
  }

  protected HomeData buidleData(LayoutBlock config, HomeData.SuperData superData) {

    Integer sort = ObjectUtils.defaultIfNull(config.getSort(), 0);
    HomeData homeData =
        HomeData.builder()
            .id(config.getId())
            .title(config.getTitle())
            .titleLink(config.getTitleLink())
            .titleScreen(config.getTitleScreen())
            .tab("HOT")
            .sort(sort)
            .template(config.getType())
            .typeData(config.getType())
            .bannerScreen(config.getBanner())
            .data(superData)
            .banner(config.getBanner())
            .bannerLink(config.getBannerLink())
            .bannerApp(config.getBannerMobile())
            .background(config.getBackground())
            .build();
    return homeData;
  }

  public void getProducts(
      LayoutBlockGroupMongo productGroup, String userId, List<ProductEs> products, Integer size)
      throws Exception {
    ProductGroup group = productGroup.getGroup();
    if (group != null) switch (group.getType()) {
        case "PICK":
            pickProducts(productGroup, products, size, 0);
            break;
        case "CONFIG":
            getProductsConfigs(productGroup, products, size, 0);
            break;
        default:
            break;
    }
  }

  void getProductsByAi(String userId, List<ProductEs> products, Integer size) throws Exception {
    size = Objects.isNull(size) || size <= 0 ? 10 : size;
    PageRequest page = PageRequest.of(0, size);
    Page<ProductEs> productEs = suggestionService.forHome(userId, page);
    products.addAll(productEs.getContent());
  }

  void pickProducts(
      LayoutBlockGroupMongo productGroup, List<ProductEs> products, Integer size, int offset) {
    size = Objects.isNull(size) || size <= 0 ? 10 : size;
    double rate = productGroup.getRate();
    int thisSize = (int) Math.round(size * rate / 100);
    List<Long> productIds = productGroup.getProducts();
    if (productIds == null) return;
    if ((offset * thisSize > productIds.size())) return;
    List<Long> ids =
        productIds.size() > thisSize ? productIds.subList(offset * thisSize, thisSize) : productIds;
    List<ProductEs> productEs = productRepository.findByIdInAndState(ids, "PUBLIC");
    productEs = ProductStaticService.removeAuctionStopped(productEs);
    products.addAll(productEs);
    if (productEs.size() != size) {
      offset++;
      pickProducts(productGroup, products, size, offset);
    }
  }

  void getProductsConfigs(
      LayoutBlockGroupMongo productGroup, List<ProductEs> products, Integer size, Integer offset) {
    size = Objects.isNull(size) || size <= 0 ? 10 : size;
    double rate = productGroup.getRate();
    int thisSize = (int) Math.round(size * rate / 100);
    PageRequest page = PageRequest.of(0, thisSize);
    ProductGroup group = productGroup.getGroup();
    ProductGroupMongo.Rules rules = group.getRules();
    List<ProductGroupMongo.Rules.Condition> conditions = rules.getProducts();
    List<Integer> categoryId = rules.getCategories();
    BoolQueryBuilder queryBuilder = boolQuery().filter(termQuery("state", "PUBLIC"))
            .mustNot(termQuery("shop.isLock", true))
            .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)));
    if (Objects.nonNull(conditions)) {
      List<String> types = new ArrayList<>();
      conditions.forEach(condition -> {
        if (condition.getType().equals("ALL")) {
          types.addAll(ProductUtils.PRODUCT_ALL_TYPE_STR);
        } else {
          if (!types.contains(condition.getType())) {
            types.add(condition.getType());
          }
        }
      });
      conditions.forEach(
              condition -> {
                ElasticsearchBuilderQuery.queryByType(types, queryBuilder);
                ElasticsearchBuilderQuery.queryByState(condition, queryBuilder);
                ElasticsearchBuilderQuery.queryByCondition(condition.getCondition(), queryBuilder);
              });
    }
    if (Objects.nonNull(categoryId))
        if (!categoryId.isEmpty()) ElasticsearchBuilderQuery.queryByCategories(categoryId, queryBuilder);
    SortBuilder<FieldSortBuilder> sortBuilder =
        SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    SearchQuery searchQuery =
        ElasticsearchBuilderQuery.searchQueryBuilder(queryBuilder, sortBuilder, page);
    Page<ProductEs> productEs = productRepository.search(searchQuery);
    List<ProductEs> productEsList = productEs.getContent();
    productEsList = ProductStaticService.removeAuctionStopped(productEs);
    products.addAll(productEsList);
    if (productEsList.size() != size) {
      offset++;
      pickProducts(productGroup, productEsList, size, offset);
    }
  }

  protected Page<ProductEs> getData(List<String> type, Pageable pageable) {
    SortBuilder sortBuilder = SortBuilders.fieldSort("id").order(SortOrder.DESC);
    BoolQueryBuilder queryBuilders =
        QueryBuilders.boolQuery()
                .mustNot(termQuery("shop.isLock", true))
            .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
            .filter(termQuery("state", "PUBLIC"))
            .filter(termsQuery("type", type));
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilders)
            .withIndices(this.productIndex())
            .withTypes(this.productType())
            .withPageable(pageable)
            .withSort(sortBuilder)
            .build();
    return productRepository.search(searchQuery);
  }

  protected QueryBuilder getQueryBuilder(
      Integer categoryId, List<String> type, List<Integer> attributes) {
    if (attributes == null || attributes.size() == 0) return QueryBuilders.boolQuery()
            .mustNot(termQuery("shop.isLock", true))
            .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
            .filter(termQuery("categories.id", categoryId))
            .filter(termQuery("state", "PUBLIC"))
            .filter(termsQuery("type", type));
    else return QueryBuilders.boolQuery()
            .mustNot(termQuery("shop.isLock", true))
            .filter(boolQuery().should(termQuery("isQuantityLimited", false)).should(rangeQuery("remainingQuantity").gt(0)))
            .filter(termQuery("categories.id", categoryId))
            .filter(termQuery("state", "PUBLIC"))
            .filter(termsQuery("type", type))
            .filter(termsQuery("attributes.value_id", attributes));
  }

  protected List<String> getType(String types) {
    List<String> type = new ArrayList<>();
    if (types.equals("ALL")) {
      type.add("NORMAL");
      type.add("CLASSIFIER");
      type.add("PROMOTION");
      type.add("SPECIAL");
      type.add("AUCTION");
      type.add("AUCTION_SALE");
    } else if (types.equals("AUCTION")) {
      type.add("AUCTION");
      type.add("AUCTION_SALE");
    } else if (types.equals("NORMAL")) {
      type.add("NORMAL");
      type.add("CLASSIFIER");
      type.add("PROMOTION");
      type.add("SPECIAL");
    }
    return type;
  }

  protected SortBuilder sortBuilder(String orderBy) {
    SortBuilder sortPrince = null;
    if (orderBy.equals("asc")) sortPrince = SortBuilders.fieldSort("variants.salePrice").order(SortOrder.ASC);
    else if (orderBy.equals("desc")) sortPrince = SortBuilders.fieldSort("variants.salePrice").order(SortOrder.DESC);
    else
        sortPrince = SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    return sortPrince;
  }

  protected void addStatusLikeProduct(List<ProductEs> products, String userId) {
    ProductLike productLike = productLikeMDRepository.findById(userId).orElse(null);
    List<Long> productIds =
        Objects.isNull(productLike) ? new ArrayList<>() : productLike.getProductIds();
    products.forEach(
        productEs -> {
          if (productIds.contains(productEs.getId())) productEs.setIsLiked(true);
          else productEs.setIsLiked(false);
        });
  };
}
