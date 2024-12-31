package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCartRequest {
    Long productId;
    int quantity;
    Double unitPrice;
    Double totalPrice;
    List<Attributes> attributes;

    @Data
    public static class Attributes {
        @NotBlank(message = "Tên biến thể không được để trống")
        private String name;
        @NotBlank(message = "Giá trị biến thể không được để trống")
        private String value;

    }
}
