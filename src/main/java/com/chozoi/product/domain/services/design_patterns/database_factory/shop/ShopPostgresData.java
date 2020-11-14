package com.chozoi.product.domain.services.design_patterns.database_factory.shop;

import com.chozoi.product.domain.entities.abstracts.Shop;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.repositories.postgres.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopPostgresData implements ShopFactory {
    @Autowired
    ShopRepository repository;

    @Override
    public Shop getData(Integer shopId) throws Exception {
        return repository.findById(shopId).orElseThrow(() -> new Exception(ExceptionMessage.shopNotFound(shopId)));
    }
}
