package com.chozoi.product.app.responses;

import lombok.Data;

import java.util.List;

@Data
public class InternalResponse {
    private Boolean status;
    private List<Long> keep_ids;

    public InternalResponse(boolean status, List<Long> ids) {
        this.status = status;
        this.keep_ids = ids;
    }

}
