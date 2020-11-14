package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ProductReportIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReportIssueRepository extends JpaRepository<ProductReportIssue, Long> {
  List<ProductReportIssue> findByProductId(Long id);

  List<ProductReportIssue> findByProductIdOrderByIdDesc(Long id);
}
