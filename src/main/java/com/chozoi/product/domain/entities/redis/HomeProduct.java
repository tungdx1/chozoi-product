package com.chozoi.product.domain.entities.redis;

import com.chozoi.product.data.response.ProductsPublicResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "ChozoiProductsHome", timeToLive = 60)
public class HomeProduct implements Serializable {

    @Id
    private String criterian;

    private List<ProductsPublicResponse> products;
}
