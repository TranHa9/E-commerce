package vn.techmaster.tranha.ecommerce.resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.dto.CartItemDto;
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
    public ResponseEntity<?> getCartItemByUserId(@PathVariable Long userId) throws Exception {

        List<CartItemDto> response = cartItemService.getCartItemByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        if (response == null) {
            result.put("message", "Cart not found for user with ID " + userId);
            result.put("data", response);
            return ResponseEntity.ok(result);
        }
        if (response.isEmpty()) {
            result.put("message", "Cart is empty for user with ID " + userId);
            result.put("data", response);
            return ResponseEntity.ok(result);
        }

        result.put("status", "success");
        result.put("data", response);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
    }
}
