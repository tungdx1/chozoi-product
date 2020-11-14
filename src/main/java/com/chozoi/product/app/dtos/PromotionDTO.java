package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PromotionDTO {

    private Long id;

    @JsonProperty("price")
    @Positive(message = "price must be positive")
    private Long price;

    @JsonProperty("sale_price")
    @Positive(message = "sale_price must be positive")
    private Long salePrice;

    @JsonProperty("date_start")
    @Future
    private LocalDateTime dateStart;

    @JsonProperty("date_end")
    @Future
    private LocalDateTime dateEnd;


}
