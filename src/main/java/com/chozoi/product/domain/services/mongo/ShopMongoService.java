package com.chozoi.product.domain.services.mongo;

import com.chozoi.product.domain.entities.mongodb.Shop;
import com.chozoi.product.domain.entities.mongodb.ShopStats;
import com.chozoi.product.domain.entities.redis.ShopRedis;
import com.chozoi.product.domain.entities.redis.ShopStatsRedis;
import com.chozoi.product.domain.factories.BeanMapper;
import com.chozoi.product.domain.repositories.mongodb.ShopMDRepository;
import com.chozoi.product.domain.repositories.mongodb.ShopStatsMDRepository;
import com.chozoi.product.domain.repositories.redis.ShopRedisRepository;
import com.chozoi.product.domain.repositories.redis.ShopStatsRedisRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopMongoService {
    @Autowired
    protected ShopMDRepository shopMDRepository;

    @Autowired
    protected ShopStatsMDRepository shopStatsMDRepository;

    @Autowired
    protected ShopRedisRepository shopRedisRepository;

    @Autowired
    protected ShopStatsRedisRepository shopStatsRedisRepository;

    @Autowired
    private BeanMapper beanMapper;

    public Shop getById(int shopId) throws NotFoundException {
        ShopRedis shop = shopRedisRepository.findById(shopId).orElse(null);
        if (shop == null) {
            Shop shopMongo = shopMDRepository.findById(shopId).orElseThrow(() -> new NotFoundException("Shop id: " + shopId + " not found"));
            shopRedisRepository.save(beanMapper.shopMongoToRedis(shopMongo));
            return shopMongo;
        } else {
            return beanMapper.shopRedisToMongo(shop);
        }
    }

    public ShopStats getStats(int shopId) {
        ShopStatsRedis shopStats = shopStatsRedisRepository.findById(shopId).orElse(null);
        if (shopStats == null) {
            ShopStats stats = shopStatsMDRepository.findById(shopId).orElse(new ShopStats());
            shopStatsRedisRepository.save(beanMapper.shopStatsMongoToRedis(stats));
            return stats;
        } else {
            return beanMapper.shopStatsRedisToMongo(shopStats);
        }
    }
}
