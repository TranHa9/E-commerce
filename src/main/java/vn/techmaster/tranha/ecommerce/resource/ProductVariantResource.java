package vn.techmaster.tranha.ecommerce.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductVariantRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductVariantResponse;
import vn.techmaster.tranha.ecommerce.service.ProductVariantService;

@RestController
@RequestMapping("/api/v1/product-variants")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantResource {

    ProductVariantService productVariantService;
    ObjectMapper objectMapper;

    @PostMapping
    public ProductVariantResponse createProductVariant(
            @RequestPart("image") MultipartFile image,
            @RequestPart("request") @Valid String createProductVariantRequest
    ) throws Exception {
        try {
            CreateProductVariantRequest request = objectMapper.readValue(createProductVariantRequest, CreateProductVariantRequest.class);
            return productVariantService.createProductVariant(image, request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ", e);
        }
    }
}
