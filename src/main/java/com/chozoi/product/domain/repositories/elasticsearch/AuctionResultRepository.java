package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.AuctionResultData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionResultRepository extends ElasticsearchRepository<AuctionResultData, UUID> {
  Page<AuctionResultData> findByWinnerIdOrderByCreatedAtDesc(Long winnerId, Pageable page);
  List<AuctionResultData> findByWinnerIdOrderByCreatedAtDesc(Long winnerId);
}
