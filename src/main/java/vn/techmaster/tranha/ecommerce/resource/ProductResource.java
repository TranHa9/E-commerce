package vn.techmaster.tranha.ecommerce.resource;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.request.ProductSearchRequest;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductResource {

    ProductService productService;
    ObjectMapper objectMapper;

    @GetMapping
    public CommonSearchResponse<?> search(ProductSearchRequest request) {
        return productService.searchProduct(request);
    }

    @PostMapping
    public ProductResponse createProduct(
            @RequestPart("product") String createProductRequest,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) throws Exception {
        try {
            CreateProductRequest request = objectMapper.readValue(createProductRequest, CreateProductRequest.class);
            return productService.createProduct(request, images);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ", e);
        }
    }
}
