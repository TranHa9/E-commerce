package vn.techmaster.tranha.ecommerce.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.model.response.CartItemResponse;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDto {

    Long id;
    Long cartId;
    Long productId;
    int quantity;
    Double unitPrice;
    Double totalPrice;
    int maxQuantity;
    Boolean isUpdated;
    Boolean isInactive;
    String productName;
    List<String> imageUrls;
    List<Attributes> variants;
    private Long shopId;
    private String shopName;

    @Data
    public static class Attributes {
        private String name;
        private String value;

    }
}
