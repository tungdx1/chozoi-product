package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.InstantBidEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface AuctionInstantBidRepository extends ElasticsearchRepository<InstantBidEs, Long> {
  List<InstantBidEs> findByUserIdAndAuctionIdOrderByCreatedAtDesc(Integer userId, Long auctionId);
}
