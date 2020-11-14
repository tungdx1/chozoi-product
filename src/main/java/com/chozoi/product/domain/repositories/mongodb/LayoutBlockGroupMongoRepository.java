package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockGroupMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LayoutBlockGroupMongoRepository extends MongoRepository<LayoutBlockGroupMongo, Integer> {
    List<LayoutBlockGroupMongo> findAllByStateNot(String deleted);

    List<LayoutBlockGroupMongo> findAllByBlockIdAndTabIndexAndStateNot(Integer blockId, Integer tabIndex, String state);
}
