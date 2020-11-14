package com.chozoi.product.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@Message
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeProduct implements Serializable {
    @Field("id")
    private Integer id;

    private String name;

    @Field("value_id")
    private Integer value_id;

    private String value;
}
