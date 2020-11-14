package com.chozoi.product.domain.entities.redis;

import com.chozoi.product.domain.entities.mongodb.Province;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@Builder
@RedisHash(value = "Shop", timeToLive = 86400)
public class ShopRedis {
    private Integer id;

    private String name;

    private String email;

    private String contactName;

    private String phoneNumber;

    private String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Province> provinces;

    private String pageUrl;

    private String imgAvatarUrl;

    private String imgCoverUrl;

    private Long createdAt;

    private Long updatedAt;
}
