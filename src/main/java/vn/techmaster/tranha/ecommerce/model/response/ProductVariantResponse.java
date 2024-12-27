package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Product;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {

    Long id;

    Product product;

    Double price;

    int stockQuantity;

    String imageUrls;

    ProductStatus status;
}
