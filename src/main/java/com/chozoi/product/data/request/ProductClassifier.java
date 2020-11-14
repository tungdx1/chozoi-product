package com.chozoi.product.data.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductClassifier implements Serializable {

    private static final long serialVersionUID = 1289352275013778731L;
    private String name;
    private List<String> values;
}
