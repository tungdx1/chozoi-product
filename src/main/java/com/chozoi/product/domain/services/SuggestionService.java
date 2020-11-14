package com.chozoi.product.domain.services;

import com.chozoi.product.data.response.RecursiveData;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.factories.EngineFactory;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class SuggestionService {

  private final EngineFactory engineFactory;
  private final ProductEsRepository productEsRepository;
  private String state = "PUBLIC";

  @Value("${environment}")
  private String cluster;

  public SuggestionService(EngineFactory engineFactory, ProductEsRepository productEsRepository) {
    this.engineFactory = engineFactory;
    this.productEsRepository = productEsRepository;
  }

  // TODO : recomment and set ttl cache
  //  @Cacheable(value = CacheConstant.SingleCache, key = "{#userId, #pageable.pageNumber,
  // #pageable.pageSize}")
  public Page<ProductEs> forHome(String userId, Pageable pageable) throws Exception {
    // TODO : xử lí sau: khi loại bỏ sản phẩm không public;
    userId = ObjectUtils.defaultIfNull(userId, "guest");
    List<ProductEs> productEs = new ArrayList<>();
    if (cluster.equals("DEV")) productEs =
            productEsRepository
                    .findByStateOrderByIdDesc(String.valueOf(ProductState.PUBLIC), pageable)
                    .getContent();
    else if (cluster.equals("PROD")) productEs =
            getProducts(
                    new ArrayList<>(), userId, pageable.getPageSize(), pageable.getPageNumber() + 1, 1);
    final Page<ProductEs> page = new PageImpl<>(productEs);
    return page;
  }

  private List<ProductEs> getProducts(
      List<ProductEs> products, String userId, Integer size, Integer page, int i) throws Exception {
    Integer count = size * page;
    RecursiveData recursiveData = engineFactory.getIds(userId, i);
    List<Long> ids = recursiveData.getIds();
    Iterable<ProductEs> productList = productEsRepository.findByIdInAndState(ids, state);
    productList.forEach(
        productEs -> {
          if (productEs.getType().equals("AUCTION") || productEs.getType().equals("AUCTION_SALE")) {
            if (productEs.getAuction().getState().equals("BIDING")) products.add(productEs);
          } else products.add(productEs);
        });
    if (products.size() < size * page && !recursiveData.getStatus()) {
      i = i + 1;
      getProducts(products, userId, size, page, i);
    }
    List<ProductEs> response;
    if (products.size() <= count) response = products.subList(size * (page - 1), products.size());
    else
        response = products.subList(size * (page - 1), count);

    return response;
  }
}
