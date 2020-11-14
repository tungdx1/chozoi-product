package com.chozoi.product.domain.entities.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "accounts.profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private Integer id;
    private String name;
    private Integer birthday;
    private String avatarUrl;
    private String gender;
    private Long createdAt;
    private Long updatedAt;
}
