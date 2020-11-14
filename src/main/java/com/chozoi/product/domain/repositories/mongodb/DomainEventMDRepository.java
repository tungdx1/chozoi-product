package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DomainEventMDRepository extends MongoRepository<ProductLog, String> {
    Page<ProductLog> findByProductIdOrderByCreatedAtDesc(Long id, Pageable pageable);

    Page<ProductLog> findByShopIdOrderByCreatedAtDesc(Integer id, Pageable pageable);

    Page<ProductLog> findByUpdatedByIdOrderByCreatedAtDesc(Integer id, Pageable pageable);

    Page<ProductLog> findByUpdatedBySystemIdOrderByCreatedAtDesc(Integer id, Pageable pageable);

    Page<ProductLog> findByTypeOrderByCreatedAtDesc(String type, Pageable pageable);


}
