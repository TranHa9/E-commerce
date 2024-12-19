package vn.techmaster.tranha.ecommerce.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.*;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.ShopResponse;
import vn.techmaster.tranha.ecommerce.service.ShopService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/shops")
@AllArgsConstructor
public class ShopResource {

    ShopService shopService;
    ObjectMapper objectMapper;

    @GetMapping("/user/{userId}")
    public ShopResponse findByUserId(@PathVariable Long userId) {
        return shopService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public ShopResponse shopById(@PathVariable Long id) {
        return shopService.findByShopId(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody @Valid ShopCreateRequest request) {
        return shopService.createShop(id, request);

    }

    @GetMapping
    public CommonSearchResponse<?> search(ShopSearchRequest request) {
        return shopService.searchShop(request);
    }

    @PutMapping("/{id}")
    public ShopResponse update(@PathVariable Long id,
                               @RequestPart("request") String updateShopRequest,
                               @RequestPart(value = "logo", required = false) MultipartFile logo
    ) throws ObjectNotFoundException, IOException {
        try {
            UpdateShopRequest request = objectMapper.readValue(updateShopRequest, UpdateShopRequest.class);
            return shopService.updateShop(id, logo, request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ", e);
        }
    }

}
