package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductAttributesRequest {

    Long variantId;

    String name;

    String value;
}
