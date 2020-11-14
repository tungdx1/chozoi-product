package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {}
