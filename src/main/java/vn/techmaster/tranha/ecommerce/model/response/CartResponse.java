package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long id;
    int quantity;
    Double totalPrice;
    UserResponse user;
}
