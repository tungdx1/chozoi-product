package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.product_ranking.LayoutBlockProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LayoutBlockGroupRepository
    extends JpaRepository<LayoutBlockProductGroup, Integer> {
  List<LayoutBlockProductGroup> findByBlockIdAndTabIndex(Integer blockId, Integer tabIndex);
}
