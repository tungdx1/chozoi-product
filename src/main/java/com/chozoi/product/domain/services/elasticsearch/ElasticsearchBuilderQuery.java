package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.domain.entities.mongodb.config_home.ProductGroupMongo;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionType;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.utils.ProductUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@Service
public class ElasticsearchBuilderQuery {
  @Autowired private ProductEsRepository productEsRepository;

  public static void queryByType(List<String> types, BoolQueryBuilder queryBuilder) {
    queryBuilder.filter(termsQuery("type", types));
  }

  public static void queryByState(
      ProductGroupMongo.Rules.Condition condition, BoolQueryBuilder queryBuilder) {
    String type = condition.getType();
    String state = condition.getState();
    if (ProductUtils.AUCTION_TYPE_STR.contains(type)) try {
        if (state.equals("PUBLIC")) {
          state = ProductAuctionState.BIDING.toString();
        }
        ProductAuctionState.valueOf(state);
        queryBuilder.must(termQuery("auction.state", state));
    } catch (Exception e) {
    }
    else try {
        ProductState.valueOf(state);
        queryBuilder.must(termQuery("state", state));
    } catch (Exception e) {
    }
  }

  public static void queryByCondition(String condition, BoolQueryBuilder queryBuilder) {
    if (condition.equals("ALL")) {
      List<String> conditions = Arrays.asList("NEW", "USED");
      queryBuilder.filter(termsQuery("condition.keyword", conditions));
    } else if (condition.equals("USED") || condition.equals("NEW"))
        queryBuilder.filter(termQuery("condition.keyword", condition));
  }

  public static void queryByCategories(List<Integer> categoryId, BoolQueryBuilder queryBuilder) {
    queryBuilder.filter(termsQuery("categories.id", categoryId));
  }

  public static void sortBuilder(List<Integer> categoryId, BoolQueryBuilder queryBuilder) {
    queryBuilder.filter(termsQuery("categories.id", categoryId));
  }

  public static SearchQuery searchQueryBuilder(
      BoolQueryBuilder queryBuilder, SortBuilder<FieldSortBuilder> sortBuilder, PageRequest page) {
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("chozoi_products")
            .withTypes("_doc")
            .withPageable(page)
            .withSort(sortBuilder)
            .build();
    return searchQuery;
  }
}
