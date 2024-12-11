package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.entity.Shop;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, message = "Tên sản phẩm không được vượt quá 150 ký tự")
    String name;

    String description;

    @Min(value = 0, message = "Giá trị tối thiểu của giá là 0")
    @NotNull(message = "Giá sản phẩm không được để trống")
    Double minPrice;

    @Min(value = 0, message = "Giá trị tối thiểu của giá là 0")
    @NotNull(message = "Giá sản phẩm không được để trống")
    Double maxPrice;

    @Pattern(regexp = "^\\d+(?:\\.\\d{1,2})?$", message = "Giá phải là số hợp lệ (tối đa 2 chữ số thập phân)")
    String prices;

    @NotBlank(message = "URL ảnh không được để trống")
    String images;

    @Min(value = 0, message = "Số lượng tồn kho không thể là số âm")
    int stockQuantity;

    @NotBlank(message = "Xuất xứ sản phẩm không được để trống")
    String origin; //Xuất xứ sản phẩm

    @Future(message = "Hạn sử dụng phải là một ngày trong tương lai")
    LocalDate expiryDate; //Hạn sử dụng

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    Category category;
}
