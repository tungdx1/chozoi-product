package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.DomainLogEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DomainEventRepository extends JpaRepository<DomainLogEvent, UUID> {
}
