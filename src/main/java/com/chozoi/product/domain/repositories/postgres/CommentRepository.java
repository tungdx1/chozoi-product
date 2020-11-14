package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Comment;
import com.chozoi.product.domain.entities.postgres.ShippingSelect;
import com.chozoi.product.domain.entities.postgres.types.CommentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStateAndUserId(CommentState state, Integer id);
}