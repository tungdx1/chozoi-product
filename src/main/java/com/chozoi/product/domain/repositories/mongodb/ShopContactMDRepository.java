package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ShopContact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShopContactMDRepository extends MongoRepository<ShopContact, Integer> {
    List<ShopContact> findByShopId(Integer id);
}
