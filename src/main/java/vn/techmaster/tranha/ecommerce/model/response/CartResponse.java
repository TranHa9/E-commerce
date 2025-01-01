package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.CartItem;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long id;
    int quantity;
    Double totalPrice;
    List<CartItemResponse> cartItems;
    User user;
}
