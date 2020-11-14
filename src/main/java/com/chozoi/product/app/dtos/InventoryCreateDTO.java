package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
public class InventoryCreateDTO {
    @Id
    private Long id;

    @JsonProperty("initial_quantity")
    private Integer initialQuantity;

    @JsonProperty("in_quantity")
    private Integer inQuantity;

    @JsonProperty("out_quantity")
    private Integer outQuantity;

    @JsonProperty(value = "remaining_quantity", access = JsonProperty.Access.READ_ONLY)
    private Integer remainingQuantity() {
        return initialQuantity + inQuantity - outQuantity;
    }
}
