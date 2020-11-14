package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.types.ProductState;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangeStateDTO {
    @NotEmpty
    private List<Long> ids;
    @NotNull
    private ProductState state;
}
