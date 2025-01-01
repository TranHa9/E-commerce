package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.dto.CartDto;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;
import vn.techmaster.tranha.ecommerce.model.response.CartResponse;
import vn.techmaster.tranha.ecommerce.service.CartService;

@RestController
@RequestMapping("/api/v1/carts")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartResource {

    CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) throws Exception {
        CartDto cartDto = cartService.getCartByUserId(userId);
        if (cartDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cart not found for user " + userId);
        }
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/{userId}")
    public CartResponse create(@PathVariable Long userId, @RequestBody @Valid CreateCartRequest request) throws Exception {
        return cartService.createCart(userId, request);
    }

}
