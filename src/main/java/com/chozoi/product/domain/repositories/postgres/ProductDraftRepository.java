package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDraftRepository extends JpaRepository<ProductDraft, Long> {
    List<ProductDraft> findByIdInAndState(List<Long> ids, ProductState preState);
}
