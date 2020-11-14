package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "product_draft", schema = "products")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
public class ProductDraft implements Cloneable {
    @Id
    private Long id;

    @Type(type = "pg-jsonb")
    @Column(name = "data", columnDefinition = "jsonb")
    private Product data;

    @Enumerated(value = EnumType.STRING)
    @Type(type = "pg-enum")
    @Column(nullable = false, columnDefinition = "product_state", name = "state")
    private ProductState state;

    @Column(name = "updated_version")
    @Min(1)
    @NotNull
    private Integer updatedVersion;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @SneakyThrows
    @Override
    public ProductDraft clone() throws CloneNotSupportedException {
        try {
            return (ProductDraft) super.clone();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
