package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductVariantRequest {

    Long productId;

    String attributes;

    String imageUrls;

    int stockQuantity;
}