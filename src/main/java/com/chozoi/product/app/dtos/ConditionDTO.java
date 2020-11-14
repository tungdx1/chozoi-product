package com.chozoi.product.app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class ConditionDTO {
    @Size(min = 2, max = 2)
    private List<Long> price;

    @Min(1)
    @Max(5)
    private Integer rating;

    private List<Integer> place;

    private String condition;
}
