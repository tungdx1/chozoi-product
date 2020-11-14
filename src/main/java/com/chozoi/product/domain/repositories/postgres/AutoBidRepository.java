package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AutoBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoBidRepository extends JpaRepository<AutoBid, Long> {

  List<AutoBid> findByUserIdAndPhaseIdIn(Integer userId, List<Long> phaseIds);
}
