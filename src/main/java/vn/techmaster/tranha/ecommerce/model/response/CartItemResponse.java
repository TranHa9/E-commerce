package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long id;
    int quantity;
    Double unitPrice;
    Double totalPrice;
    String variants;
    ProductResponse product;
}
