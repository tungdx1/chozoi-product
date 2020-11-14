package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.types.InventoryHistoryState;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class InventoryProductDTO {

    @JsonProperty("state")
    @NotNull
    private InventoryHistoryState state;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private List<InvetoryChangeQuantity> data;


}
