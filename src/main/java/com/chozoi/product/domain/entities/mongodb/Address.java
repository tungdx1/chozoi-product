package com.chozoi.product.domain.entities.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts.address")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    private Integer id;

    private String detailAddress;

    private Integer wardId;

    private Integer districtId;

    private Integer provinceId;

    private Long createdAt;

    private Long updatedAt;
}
