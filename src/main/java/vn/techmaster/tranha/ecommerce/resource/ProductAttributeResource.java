package vn.techmaster.tranha.ecommerce.resource;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductAttributesRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductAttributeResponse;
import vn.techmaster.tranha.ecommerce.service.ProductAttributeService;

@RestController
@RequestMapping("/api/v1/product-attributes")
@AllArgsConstructor
public class ProductAttributeResource {
    ProductAttributeService productAttributeService;

    @PostMapping
    public ResponseEntity<?> createProductAttribute(@RequestBody @Valid CreateProductAttributesRequest request) throws Exception {
        ProductAttributeResponse productAttribute = productAttributeService.createProductAttribute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productAttribute);
    }
}
