package com.chozoi.product.domain.entities.postgres;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

@Entity
@Table(schema = "auctions", name = "last_minute_bid")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastMinuteBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "price_step")
    private Long priceStep;

    @Column(name = "interval_to_end")
    private Integer intervalToEnd;

    @Column(name = "max_bids")
    private Integer maxBids;

    @Column(name = "max_price")
    private Long maxPrice;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Type(type = "pg-uuid")
    private UUID uuid;

    @Column(name = "user_id")
    private Integer userId;
}
