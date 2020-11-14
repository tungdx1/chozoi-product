package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AuctionParticipant;
import com.chozoi.product.domain.values.AuctionParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuctionParticipantRepository
        extends JpaRepository<AuctionParticipant, AuctionParticipantId> {

    @Query(value = "SELECT a FROM AuctionParticipant a WHERE a.id.userId = ?1")
    Page<AuctionParticipant> findByUserId(Integer userId, Pageable pageable);
}
