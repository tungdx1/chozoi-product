package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ShippingPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingPartnerRepository extends JpaRepository<ShippingPartner, Integer> {
}
