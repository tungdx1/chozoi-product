package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class InventoryDTO {
    @Id
    private Long id;

    @JsonProperty("initial_quantity")
    private Integer initialQuantity;

    @JsonProperty("in_quantity")
    @Min(0)
    @Max(999)
    @NotNull
    private Integer inQuantity;

    @JsonProperty("out_quantity")
    private Integer outQuantity;

    @JsonProperty(value = "remaining_quantity", access = JsonProperty.Access.READ_ONLY)
    private Integer remainingQuantity() {
        return inQuantity - outQuantity;
    }
}
