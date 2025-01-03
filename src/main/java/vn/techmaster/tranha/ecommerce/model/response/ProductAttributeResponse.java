package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttributeResponse {
    Long id;

    ProductVariant productVariant;

    String name;

    String value;
}
