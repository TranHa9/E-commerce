package vn.techmaster.tranha.ecommerce.resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.dto.CartItemDto;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.response.CartItemResponse;
import vn.techmaster.tranha.ecommerce.service.CartItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cartItems")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)

public class CartItemResource {
    CartItemService cartItemService;

    @GetMapping("/{userId}")
    public List<CartItemDto> getCartItemByUserId(@PathVariable Long userId) throws Exception {
        return cartItemService.getCartItemByUserId(userId);
    }

    @DeleteMapping("/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
    }

    @PatchMapping("/{cartItemId}")
    public void updateCartItemQuantity(@PathVariable Long cartItemId, @RequestBody int quantity) throws Exception {
        cartItemService.updateCartItemQuantity(cartItemId, quantity);
    }
}
