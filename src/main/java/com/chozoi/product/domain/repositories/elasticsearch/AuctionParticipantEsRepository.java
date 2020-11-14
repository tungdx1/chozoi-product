package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.AuctionParticipantEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionParticipantEsRepository
    extends ElasticsearchRepository<AuctionParticipantEs, UUID> {
  List<AuctionParticipantEs> findByUserId(Integer userId);

  Page<AuctionParticipantEs> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

  Page<AuctionParticipantEs> findByUserIdOrderByUpdatedAtDesc(Integer userId, Pageable pageable);

  List<AuctionParticipantEs> findByUserIdAndAuctionIdIn(Integer userId, List<Long> dis);

  Page<AuctionParticipantEs> findByAuctionIdInAndUserId(List<Long> auctionId, Integer userId, Pageable pageable);

  List<AuctionParticipantEs> findByAuctionId(Long auctionId);

  Page<AuctionParticipantEs> findByAuctionIdOrderByCreatedAtDesc(Long auctionId, Pageable pageable);

  Page<AuctionParticipantEs> findByAuctionIdOrderByUpdatedAtDesc(Long auctionId, Pageable pageable);

  Page<AuctionParticipantEs> findByUserId(Integer userId, Pageable pageable);

  Page<AuctionParticipantEs> findByPhaseIdInAndUserIdOrderByCreatedAtDesc(List<Long> phaseIds, Integer userId, Pageable pageable);
  List<AuctionParticipantEs> findAllByPhaseIdInAndUserId(List<Long> phaseIds, Integer userId);
}
