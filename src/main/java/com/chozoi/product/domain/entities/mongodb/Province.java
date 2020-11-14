package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "accounts.province")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Province {
    @Id
    private Integer id;

    private String provinceName;
}
