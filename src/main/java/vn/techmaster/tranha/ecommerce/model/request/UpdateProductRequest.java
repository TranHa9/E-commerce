package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 250, message = "Tên sản phẩm không được vượt quá 250 ký tự")
    String name;

    String description;

    @NotEmpty(message = "Ảnh phẩm không được để trống")
    List<String> imageUrls;

    @NotEmpty(message = "Danh sách giá không được để trống")
    @Valid
    List<Prices> prices;

    @NotBlank(message = "Thương hiệu sản phẩm không được để trống")
    String brand;

    @NotBlank(message = "Xuất xứ sản phẩm không được để trống")
    String origin; //Xuất xứ sản phẩm

    @Future(message = "Hạn sử dụng phải là một ngày trong tương lai")
    LocalDate expiryDate; //Hạn sử dụng

    Long categoryId;

    @Data
    public static class Prices {
        @NotEmpty(message = "Danh sách biến thể không được để trống")
        @Valid
        private List<Variants> variants;
        @Positive(message = "Giá phải lớn hơn 0")
        private double price;
        @Min(value = 0, message = "Số lượng tồn kho phải là một số không âm")
        private int stockQuantity;

    }

    @Data
    public static class Variants {
        @NotBlank(message = "Tên biến thể không được để trống")
        private String name;
        @NotBlank(message = "Giá trị biến thể không được để trống")
        private String value;
    }

    @NotEmpty(message = "Danh sách biến thể sản phẩm không được để trống")
    @Valid
    private List<UpdateProductVariantRequest> variants;
}
