package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.AuctionNotification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionActionLogRepository extends MongoRepository<AuctionNotification, UUID> {
    List<AuctionNotification> findByAuctionIdInAndAction(List<Long> ids, String action);

    List<AuctionNotification> findByAuctionIdInAndActionAndTimeRemain(List<Long> ids, String action, Integer time);
}
