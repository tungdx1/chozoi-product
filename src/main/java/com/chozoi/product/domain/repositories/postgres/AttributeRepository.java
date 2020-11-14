package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Attribute;
import com.chozoi.product.domain.entities.postgres.Category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    @Query("select u from Attribute u where u.category = ?1 and u.state = 'PUBLIC'")
    List<Attribute> findAllByCategory(Category category);
}
