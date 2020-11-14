package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.utils.PostgreSQLEnumType;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(schema = "auctions", name = "instant_bid")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
public class InstantBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "user_id")
    private Integer userId;

    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "instant_bid_type", nullable = false)
    @org.hibernate.annotations.Type(type = "pg-enum")
    private Type type;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID uuid;

    public enum Type {
        MANUAL,
        AUTO,
        AUTO_LAST_MINUTE
    }
}
