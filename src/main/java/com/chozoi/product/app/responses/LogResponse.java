package com.chozoi.product.app.responses;

import com.chozoi.product.domain.entities.mongodb.ProductLog;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class LogResponse {
    private List<ProductLog> logs;
    private Metadata metadata;

    public LogResponse(Page<ProductLog> page) {
        logs = page.getContent();
        metadata = Metadata.of(page);
    }
}
