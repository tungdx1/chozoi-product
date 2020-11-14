package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserContactRepository extends JpaRepository<UserContact, Integer> {
  List<UserContact> findByUser_Id(Integer userId);
}
