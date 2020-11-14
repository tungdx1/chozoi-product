package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShopMDRepository extends MongoRepository<Shop, Integer> {
  Shop findAllById(Integer id);

  List<Shop> findByIdIn(List<Integer> id);
}
