package vn.techmaster.tranha.ecommerce.resource;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.request.ProductSearchRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateProductRequest;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductDetailResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.service.ProductService;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductResource {

    ProductService productService;
    ObjectMapper objectMapper;

    @GetMapping
    public CommonSearchResponse<?> searchAllProduct(ProductSearchRequest request) {
        return productService.searchProductAll(request);
    }

    @GetMapping("/{id}")
    public ProductDetailResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/shop/{id}")
    public CommonSearchResponse<?> SearchByShop(@PathVariable Long id, ProductSearchRequest request) {
        return productService.searchProductByShop(id, request);
    }

    @PostMapping
    public ProductResponse createProduct(@RequestBody @Valid CreateProductRequest request) throws Exception {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) throws Exception {
        return productService.updateProduct(id, request);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateProductStatus(@PathVariable Long id, @RequestBody ProductStatus status) {
        try {
            productService.updateProductStatus(id, status);
            return ResponseEntity.ok("Product status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
