package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "shops.contact")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopContact {
    @Id
    private Integer id;

    private String name;

    private String phone;

    private Boolean isDefault;

    private String type;

    private Integer shopId;

    private Integer addressId;

    private Long createdAt;

    private Long updatedAt;
}
