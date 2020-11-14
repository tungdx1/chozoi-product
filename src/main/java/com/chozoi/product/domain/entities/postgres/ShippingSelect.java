package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.ShippingSelectStatus;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "shipping_select", schema = "sales")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class ShippingSelect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "shipping_service_id")
    private Integer shippingServiceId;

    @Enumerated(value = EnumType.STRING)
    @Type(type = "pgsql_enum")
    @Column(nullable = false, columnDefinition = "status", name = "status")
    private ShippingSelectStatus status;

    @Column(name = "use_insurance")
    private Boolean useInsurance;

}
