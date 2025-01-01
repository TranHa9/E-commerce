package vn.techmaster.tranha.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDto {
    Long id;
    int quantity;
    Double unitPrice;
    Double totalPrice;
    List<Attributes> variants;
    ProductDto product;

    @Data
    public static class Attributes {
        private String name;
        private String value;

    }
}
