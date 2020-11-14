package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "accounts.user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Integer id;

    private String name;

    private String email;

    private String contactName;

    private String phoneNumber;

    private String type;

    private String pageUrl;

    private String imgAvatarUrl;

    private String imgCoverUrl;

    private Long createdAt;

    private Long updatedAt;
}
