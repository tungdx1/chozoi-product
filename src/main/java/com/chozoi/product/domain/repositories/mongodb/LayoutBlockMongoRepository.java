package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LayoutBlockMongoRepository extends MongoRepository<LayoutBlockMongo, Integer> {
  List<LayoutBlockMongo> findBySiteAndState(String site, String state);

  List<LayoutBlockMongo> findByIdAndSiteAndState(Integer id, String site, String state);

    List<LayoutBlockMongo> findBySiteAndStateOrderBySortAsc(String site, String state);
}
