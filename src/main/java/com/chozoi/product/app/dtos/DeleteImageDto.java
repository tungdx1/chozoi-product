package com.chozoi.product.app.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeleteImageDto {
    @NotNull
    @NotEmpty
    private List<Long> ids;
}
