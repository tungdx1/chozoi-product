package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.AssertTrue;

@Data
@NoArgsConstructor
@Log4j2
public class ProductCreateDTO extends ProductDTO {

    // check size variants
    @AssertTrue(message = "Variant count not matched")
    @JsonIgnore
    protected boolean isValidSizeVariants() {
        // TODO: check
//        if (type == ProductType.CLASSIFIER) {
//            log.info(classifiers.get(0).getValues().size());
//            int countRequired = 0;
//            if (classifiers.size() == 1) {
//                countRequired += classifiers.get(0).getValues().size();
//            } else if (classifiers.size() == 2) {
//                countRequired += classifiers.get(0).getValues().size() * classifiers.get(1).getValues().size();
//            }
//            return variants.size() == countRequired;
//        }
        return true;
    }

    // check variant value is classifier and count classifier
    @AssertTrue(message = "Variant name or value not matched")
    @JsonIgnore
    protected boolean isValidNameVariants() {
        // TODO: check
//        if (type == ProductType.CLASSIFIER) {
//            List<String> stringList = new ArrayList<>();
//            for (VariantDTO variant : variants) {
//                int i = 0;
//                StringBuilder str = new StringBuilder();
//                for (AttributeVariant attributeVariant : variant.getAttributes()) {
//                    if (!attributeVariant.getName().equals(classifiers.get(i).getName())) {
//                        return false;
//                    }
//                    if (!classifiers.get(i).getValues().contains(attributeVariant.getValue())) {
//                        return false;
//                    } else {
//                        str.append(attributeVariant.getValue());
//                    }
//                    i += 1;
//                }
//                if (stringList.contains(str.toString())) {
//                    return false;
//                } else {
//                    stringList.add(str.toString());
//                }
//            }
//        }
        return true;
    }
}
