package com.chozoi.product.domain.entities.mongodb;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "products.product_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class ProductLog {
    private String id;
    private Short version;
    private String aggregate;
    private Long productId;
    private Integer shopId;
    private Integer updatedById;
    private Integer updatedBySystemId;
    private Object content;
    private String type;
    private Long createdAt;
}
