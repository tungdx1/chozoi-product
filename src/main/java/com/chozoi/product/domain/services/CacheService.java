package com.chozoi.product.domain.services;

import com.chozoi.product.domain.entities.redis.InventoryRedis;
import com.chozoi.product.domain.entities.redis.ProductImageRedis;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CacheService extends BaseService {
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    void cleanAllProductCache(Long productId) {
        // clean product
        productRedisRepository.deleteById(productId);
        // clean product image
        List<ProductImageRedis> imageRedis = productImageRedisRepository.findByProductId(productId);
        productImageRedisRepository.deleteAll(imageRedis);
        // clean product inventory
        List<InventoryRedis> inventory = inventoryRedisRepository.findByProductId(productId);
        inventoryRedisRepository.deleteAll(inventory);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    void cleanInventoryCache(Long productId) {
        // clean product inventory
        List<InventoryRedis> inventory = inventoryRedisRepository.findByProductId(productId);
        inventoryRedisRepository.deleteAll(inventory);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    void cleanImageCache(Long productId) {
        // clean product image
        List<ProductImageRedis> imageRedis = productImageRedisRepository.findByProductId(productId);
        productImageRedisRepository.deleteAll(imageRedis);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    void cleanProductCache(Long productId) {
        // clean product
        productRedisRepository.deleteById(productId);
    }
}
