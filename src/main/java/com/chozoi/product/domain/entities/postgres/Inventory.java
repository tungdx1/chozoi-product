package com.chozoi.product.domain.entities.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "inventory", schema = "products")
public class Inventory implements Cloneable {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @JsonIgnore
    @MapsId
    private ProductVariant variant;

    @Column(name = "initial_quantity")
    private Integer initialQuantity;

    @Column(name = "in_quantity")
    private Integer inQuantity;

    @Column(name = "out_quantity")
    private int outQuantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public static Inventory build(Inventory inventory) {
        Inventory newInventory = new Inventory();
        newInventory.setOutQuantity(0);
        newInventory.setInitialQuantity(0);
        newInventory.setInQuantity(inventory.getInQuantity());
        return newInventory;
    }

    @Override
    public Inventory clone() throws CloneNotSupportedException {
        return (Inventory) super.clone();
    }

}
