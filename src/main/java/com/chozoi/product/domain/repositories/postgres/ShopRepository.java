package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    Shop findAllById(Integer shopId);
}
