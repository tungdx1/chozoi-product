package com.chozoi.product.domain.services.design_patterns.database_factory.config_home;

import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockGroupMongo;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockMongo;
import com.chozoi.product.domain.entities.mongodb.config_home.ProductGroupMongo;
import com.chozoi.product.domain.entities.redis.config_home.LayoutBlockRedis;
import com.chozoi.product.domain.entities.redis.config_home.ProductGroupRedis;
import com.chozoi.product.domain.repositories.mongodb.LayoutBlockGroupMongoRepository;
import com.chozoi.product.domain.repositories.mongodb.LayoutBlockMongoRepository;
import com.chozoi.product.domain.repositories.mongodb.ProductGroupMongoRepository;
import com.chozoi.product.domain.repositories.redis.LayoutBlockRedisRepository;
import com.chozoi.product.domain.repositories.redis.ProductGroupRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigHomeMongo implements ConfigHome {
  @Autowired private LayoutBlockMongoRepository layoutBlockMongoRepository;
  @Autowired private LayoutBlockGroupMongoRepository layoutBlockGroupMongoRepository;
  @Autowired private ProductGroupMongoRepository productGroupMongoRepository;

  @Autowired private LayoutBlockRedisRepository redisRepository;
  @Autowired private ProductGroupRedisRepository productGroupRedisRepository;

  /** @return config or null for next level */
  @Override
  public Object getData() {
    // get mongo
    List<ProductGroupMongo> productGroupMongoList = productGroupMongoRepository.findAll();
    List<LayoutBlockGroupMongo> layoutBlockGroupMongoList =
        layoutBlockGroupMongoRepository.findAllByStateNot("DELETED");
    List<LayoutBlockMongo> layoutBlockMongoList =
        layoutBlockMongoRepository.findBySiteAndState("home", "SHOW");
    try {
      addRelation(layoutBlockMongoList, layoutBlockGroupMongoList, productGroupMongoList);
    } catch (Exception e) {
      return null;
    }
    if (layoutBlockMongoList.size() == 0) return null;
    // set redis
    List<LayoutBlockRedis> configHomeRedis = new ArrayList<>();
    ModelMapper mapper = new ModelMapper();
    layoutBlockMongoList.forEach(
        layoutBlockMongo -> {
          LayoutBlockRedis layoutBlockRedis = mapper.map(layoutBlockMongo, LayoutBlockRedis.class);
          configHomeRedis.add(layoutBlockRedis);
        });
    List<ProductGroupRedis> productGroupRedis = new ArrayList<>();
    productGroupMongoList.forEach(
        productGroupMongo -> {
          ProductGroupRedis groupRedis = mapper.map(productGroupMongo, ProductGroupRedis.class);
          productGroupRedis.add(groupRedis);
        });
    redisRepository.saveAll(configHomeRedis);
    productGroupRedisRepository.saveAll(productGroupRedis);
    return layoutBlockMongoList;
  }

  private void addRelation(
      List<LayoutBlockMongo> configs,
      List<LayoutBlockGroupMongo> layouts,
      List<ProductGroupMongo> groups) {
    layouts.forEach(
        layout -> {
          List<ProductGroupMongo> groupsList =
              groups.stream()
                  .filter(group -> group.getId().equals(layout.getGroupId()))
                  .collect(Collectors.toList());
          if (groupsList.size() > 0) {
            ProductGroupMongo group = groupsList.get(0);
            layout.setGroup(group);
          }
        });
    configs.forEach(
        config -> {
          List<LayoutBlockGroupMongo> layoutList =
              layouts.stream()
                  .filter(layout -> layout.getBlockId().equals(config.getId()))
                  .collect(Collectors.toList());
          config.setProductGroups(layoutList);
        });
  }
}
