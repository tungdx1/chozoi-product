package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.responses.LogResponse;
import com.chozoi.product.domain.entities.mongodb.ProductLog;
import com.chozoi.product.domain.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
public class LogController {
    @Autowired
    private LogService service;

    @GetMapping(
            path = "/products",
            params = {"collection=log"})
    public LogResponse getAll(Pageable pageable) {
        Page<ProductLog> page = service.getAll(pageable);
        return new LogResponse(page);
    }

    @GetMapping(
            path = "/products/{id}",
            params = {"collection=log"})
    public LogResponse byProduct(@PathVariable(name = "id") Long productId, Pageable pageable) {
        Page<ProductLog> page = service.byProduct(productId, pageable);
        return new LogResponse(page);
    }

    @GetMapping(
            path = "/products/{id}/shops",
            params = {"collection=log"})
    public LogResponse byShop(@PathVariable(name = "id") Integer shopId, Pageable pageable) {
        Page<ProductLog> page = service.byShop(shopId, pageable);
        return new LogResponse(page);
    }

    @GetMapping(
            path = "/products/{id}/users",
            params = {"collection=log"})
    public LogResponse byUser(@PathVariable(name = "id") Integer id, Pageable pageable) {
        Page<ProductLog> page = service.byUserId(id, pageable);
        return new LogResponse(page);
    }

    @GetMapping(
            path = "/products/{id}/system",
            params = {"collection=log"})
    public LogResponse bySystem(@PathVariable(name = "id") Integer id, Pageable pageable) {
        Page<ProductLog> page = service.bySystemUserId(id, pageable);
        return new LogResponse(page);
    }
}
