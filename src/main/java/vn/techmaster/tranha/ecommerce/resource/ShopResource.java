package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.model.request.ShopCreateRequest;
import vn.techmaster.tranha.ecommerce.model.response.ShopResponse;
import vn.techmaster.tranha.ecommerce.service.ShopService;

@RestController
@RequestMapping("/api/v1/shops")
@AllArgsConstructor
public class ShopResource {

    ShopService shopService;

    @GetMapping("/{userId}")
    public ShopResponse findByUserId(@PathVariable Long userId) {
        return shopService.findByUserId(userId);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody @Valid ShopCreateRequest request) {
        return shopService.createShop(id, request);

    }
}
