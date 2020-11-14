package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.EventType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.BinaryType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Embeddable
@Table(schema = "products", name = "domain_event")
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-binary", typeClass = BinaryType.class)

public class DomainLogEvent {
    @Id
    private UUID id;

    @Column(name = "version")
    private Short version;

    @Column(name = "aggregate")
    private String aggregate;

    @Column(name = "content")
    private byte[] content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "type")
    @Type(type = "pg-enum")
    private EventType type;

}
