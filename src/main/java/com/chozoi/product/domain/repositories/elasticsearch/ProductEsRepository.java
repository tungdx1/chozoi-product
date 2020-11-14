package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductEsRepository extends ElasticsearchRepository<ProductEs, Long> {
  List<ProductEs> findByIdInAndState(List<Long> ids, String state);

  List<ProductEs> findByAuction_State(String state);

  List<ProductEs> findByShop_IdAndState(Integer id, String state);

  List<ProductEs> findByIdInAndStateAndShop_Id(List<Long> ids, String state, Integer shopId);

  Page<ProductEs> findByIdInAndState(List<Long> ids, String state, Pageable pageable);

  Page<ProductEs> findByAuction_Result_WinnerId(Integer id, Pageable pageable);

  Page<ProductEs> findByIdInAndAuction_StateAndState(
      List<Long> ids, String stateAuction, String stateProduct, Pageable pageable);

  Page<ProductEs> findByAuction_RefusePayment(Boolean state, Pageable pageable);

  List<ProductEs> findByAuction_RefusePayment(Boolean state);

  Page<ProductEs> findByIdIn(List<Long> ids, Pageable pageable);

  Page<ProductEs> findByStateOrderByIdDesc(String state, Pageable pageable);

  Page<ProductEs> findByType(String type, Pageable pageable);

  Page<ProductEs> findByStateAndIdIn(String state, List<Long> ids, Pageable pageable);

  Page<ProductEs> findByIdInAndAuction_RefusePayment(List<Long> ids, boolean b, Pageable pageable);

  List<ProductEs> findByAuction_StateAndState(String biding, String aPublic);
}
