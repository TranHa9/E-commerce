package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {
    Long id;

    String variantValue;

    Double price;

    int stockQuantity;

    String image;

    VariantStatus status;
}
