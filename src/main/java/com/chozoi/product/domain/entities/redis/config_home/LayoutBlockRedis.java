package com.chozoi.product.domain.entities.redis.config_home;

import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@NoArgsConstructor
@RedisHash(value = "chozoi_config_home", timeToLive = 86400)
public class LayoutBlockRedis extends LayoutBlock {}
