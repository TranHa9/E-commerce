package vn.techmaster.tranha.ecommerce.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.model.response.CartItemResponse;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDto {
    Long id;
    int quantity;
    Double totalPrice;
    Long userId;
    List<CartItemDto> cartItems;
}
