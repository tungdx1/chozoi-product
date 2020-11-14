package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.AuctionResultMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionResultMDRepository extends MongoRepository<AuctionResultMongo, Long> {
}
