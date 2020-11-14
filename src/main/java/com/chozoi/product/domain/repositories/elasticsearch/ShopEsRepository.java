package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.ShopEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ShopEsRepository extends ElasticsearchRepository<ShopEs, Integer> {
  List<ShopEs> findByIdIn(List<Integer> ids);
}
