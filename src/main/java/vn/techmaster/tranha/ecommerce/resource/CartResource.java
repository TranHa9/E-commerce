package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;
import vn.techmaster.tranha.ecommerce.model.response.CartResponse;
import vn.techmaster.tranha.ecommerce.service.CartService;

@RestController
@RequestMapping("/api/v1/carts")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartResource {

    CartService cartService;

    @PostMapping("/{userId}")
    public CartResponse create(@PathVariable Long userId, @RequestBody @Valid CreateCartRequest request) throws Exception {
        return cartService.createCart(userId, request);
    }

}
