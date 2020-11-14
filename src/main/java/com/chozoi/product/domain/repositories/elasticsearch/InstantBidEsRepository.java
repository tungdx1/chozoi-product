package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.InstantBidEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface InstantBidEsRepository extends ElasticsearchRepository<InstantBidEs, Long> {
  List<InstantBidEs> findFirstByUserIdAndAuctionIdOrderByCreatedAtDesc(Integer id, Long auctionId);

  InstantBidEs findInstantBidEsByUserIdAndAndAuctionId(Integer id, Long auctionId);
}
