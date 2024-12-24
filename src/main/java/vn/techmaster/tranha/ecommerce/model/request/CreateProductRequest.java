package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, message = "Tên sản phẩm không được vượt quá 150 ký tự")
    String name;

    String description;

    List<Price> prices;

    @NotBlank(message = "Thương hiệu sản phẩm không được để trống")
    String brand;

    @NotBlank(message = "Xuất xứ sản phẩm không được để trống")
    String origin; //Xuất xứ sản phẩm

    @Future(message = "Hạn sử dụng phải là một ngày trong tương lai")
    LocalDate expiryDate; //Hạn sử dụng

    Long categoryId;

    Long shopId;

    @Data
    public static class Price {
        private List<Variant> variant;
        private double price;
        private int stockQuantity;

    }

    @Data
    public static class Variant {
        private String name;
        private String value;

    }

    private List<ProductVariant> variants; // Các biến thể của sản phẩm
}
