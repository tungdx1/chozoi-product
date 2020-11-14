package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.product_ranking.ConfigLayoutBlock;
import com.chozoi.product.domain.entities.postgres.product_ranking.types.BlockState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigLayoutBlockRepository extends JpaRepository<ConfigLayoutBlock, Integer> {
  List<ConfigLayoutBlock> findBySiteAndState(String site, BlockState state);

  List<ConfigLayoutBlock> findByIdAndSiteAndState(Integer id, String site, BlockState state);

  List<ConfigLayoutBlock> findBySite(String site);
}
