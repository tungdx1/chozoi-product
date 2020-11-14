package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.ProductDraftEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductDraftEsRepository extends ElasticsearchRepository<ProductDraftEs, Long> {
}
