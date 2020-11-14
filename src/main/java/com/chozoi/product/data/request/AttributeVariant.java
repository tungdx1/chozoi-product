package com.chozoi.product.data.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class AttributeVariant implements Serializable {
    private static final long serialVersionUID = 4292684285946420238L;
    @Size(min = 1, max = 30)
    private String name;

    @Size(min = 1, max = 30)
    private String value;
}
