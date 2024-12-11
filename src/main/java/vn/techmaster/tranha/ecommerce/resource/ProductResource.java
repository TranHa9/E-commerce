package vn.techmaster.tranha.ecommerce.resource;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.exception.ExistedUserException;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductResource {

    ProductService productService;

//    @GetMapping
//    public CommonSearchResponse<?> search(ProductSearchRequest request) {
//        return productService.searchProduct(request);
//    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateProductRequest request) throws ExistedUserException {
        ProductResponse productResponse = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(productResponse);
    }
}
