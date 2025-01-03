package vn.techmaster.tranha.ecommerce.model.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long id;
    Long productId;
    int quantity;
    Double unitPrice;
    Double totalPrice;
    int maxQuantity;
    Boolean isUpdated;
    Boolean isInactive;
    List<Attributes> variants;

    @Data
    public static class Attributes {
        private String name;
        private String value;

    }
}
