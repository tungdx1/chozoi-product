package com.chozoi.product.app.dtos;

import com.chozoi.product.data.request.AttributeVariant;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Log4j2
public class ProductUpdateDTO extends ProductDTO {

    @NotNull
    private Long id;

    // check variant value is classifier and count classifier
    @AssertTrue(message = "Variant name or value not matched")
    @JsonIgnore
    protected boolean isValidNameVariants() {
        if (type == ProductType.CLASSIFIER) {
            List<String> stringList = new ArrayList<>();
            for (VariantDTO variant : variants) {
                int i = 0;
                StringBuilder str = new StringBuilder();
                for (AttributeVariant attributeVariant : variant.getAttributes()) {
                    if (!attributeVariant.getName().equals(classifiers.get(i).getName())) {
                        return false;
                    }
                    str.append(attributeVariant.getValue());
                    i += 1;
                }
                if (stringList.contains(str.toString())) {
                    return false;
                } else {
                    stringList.add(str.toString());
                }
            }
        }
        return true;
    }
}
