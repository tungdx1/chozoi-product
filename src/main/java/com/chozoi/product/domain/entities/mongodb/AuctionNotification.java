package com.chozoi.product.domain.entities.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document(collection = "logs.action.notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionNotification {
    @Id
    private UUID id;
    private String action;
    private Long auctionId;
    private Integer timeRemain;
}
