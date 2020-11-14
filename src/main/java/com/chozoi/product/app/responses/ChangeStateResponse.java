package com.chozoi.product.app.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChangeStateResponse {
    private List<Long> idsSuccess;
    private List<Long> idsError;
}
