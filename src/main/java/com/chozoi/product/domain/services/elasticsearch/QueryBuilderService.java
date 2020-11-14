package com.chozoi.product.domain.services.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class QueryBuilderService {
  /**
   * search by name
   *
   * @param queryBuilder
   * @param keyWord
   */
  public static void searchName(BoolQueryBuilder queryBuilder, String keyWord) {
    if (keyWord != null) queryBuilder.filter(matchPhraseQuery("name", keyWord));
  }

  /**
   * search sold off
   *
   * @param queryBuilder
   * @param aspect : soldOff
   */
  public static void soldOff(BoolQueryBuilder queryBuilder, String aspect) {
    if (aspect != null) if (!aspect.equals("soldOff")) queryBuilder.must(
            boolQuery()
                    .should(
                            boolQuery()
                                    .must(QueryBuilders.rangeQuery("remainingQuantity").gt(0))
                                    .filter(termQuery("isQuantityLimited", true)))
                    .should(termQuery("isQuantityLimited", false))
                    .minimumShouldMatch(1));
    else queryBuilder
                .filter(termQuery("isQuantityLimited", true))
                .must(QueryBuilders.rangeQuery("remainingQuantity").lte(0));
  }

  /**
   * @param queryBuilder
   * @param state WAITING, BIDING, STOPPED
   */
  public static void auctionState(BoolQueryBuilder queryBuilder, String state) {
    String auctionState = ProductStaticService.auctionState(state);
    if (Objects.nonNull(auctionState)) queryBuilder.filter(termQuery("auction.state", auctionState));
  }

  /**
   * builder query
   *
   * @param queryBuilder
   * @param pageable
   * @return
   */
  public static SearchQuery builder(
      BoolQueryBuilder queryBuilder, String index, Pageable pageable) {
    SortBuilder sortBuilder = SortBuilders.fieldSort("id").order(SortOrder.DESC);
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices(index)
            .withTypes("_doc")
            .withSort(sortBuilder)
            .withPageable(pageable)
            .build();
    return searchQuery;
  }
}
