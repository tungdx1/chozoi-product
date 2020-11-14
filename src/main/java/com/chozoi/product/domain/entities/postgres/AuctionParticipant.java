package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.chozoi.product.domain.values.AuctionParticipantId;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "auction_participant", schema = "auctions")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Data
@NoArgsConstructor
public class AuctionParticipant {

    @EmbeddedId
    private AuctionParticipantId id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "auction_participant_state", nullable = false)
    @Type(type = "pgsql_enum")
    private State state;

    @OneToOne
    @JoinColumn(name = "auction_id", insertable = false, updatable = false)
    private Product product;

    @OneToOne
    @JoinColumn(name = "auction_id", insertable = false, updatable = false)
    private AuctionResult result;

    @OneToOne
    @JoinColumn(name = "instant_bid_id", insertable = false, updatable = false)
    private InstantBid instantBid;

    @OneToOne
    @JoinColumn(name = "auto_bid_id", insertable = false, updatable = false)
    private AutoBid autoBid;

    @OneToOne
    @JoinColumn(name = "last_minute_bid_id", insertable = false, updatable = false)
    private LastMinuteBid lastMinuteBid;

    public enum State {
        NORMAL,
        HIDDEN,
        DELETED
    }
}
