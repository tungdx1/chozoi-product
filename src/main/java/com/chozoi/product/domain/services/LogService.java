package com.chozoi.product.domain.services;

import com.chozoi.product.domain.entities.mongodb.ProductLog;
import com.chozoi.product.domain.repositories.mongodb.DomainEventMDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private DomainEventMDRepository repository;

    public Page<ProductLog> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ProductLog> byProduct(Long productId, Pageable pageable) {
        return repository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    public Page<ProductLog> byShop(Integer shopId, Pageable pageable) {
        return repository.findByShopIdOrderByCreatedAtDesc(shopId, pageable);
    }

    public Page<ProductLog> byUserId(Integer id, Pageable pageable) {
        return repository.findByUpdatedByIdOrderByCreatedAtDesc(id, pageable);
    }

    public Page<ProductLog> bySystemUserId(Integer id, Pageable pageable) {
        return repository.findByUpdatedBySystemIdOrderByCreatedAtDesc(id, pageable);
    }
}
