package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCartRequest {

    @NotNull(message = "ID sản phẩm không được để trống")
    Long productId;

    @Positive(message = "Số lượng phải lớn hơn 0")
    int quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @Positive(message = "Đơn giá phải lớn hơn 0")
    Double unitPrice;

    @NotNull(message = "Tổng giá không được để trống")
    @Positive(message = "Tổng giá phải lớn hơn 0")
    Double totalPrice;

    @NotNull(message = "Danh sách thuộc tính không được để trống")
    @Size(min = 1, message = "Danh sách thuộc tính phải chứa ít nhất 1 mục")
    @Valid
    List<Attributes> variants;

    @Data
    public static class Attributes {
        @NotBlank(message = "Tên biến thể không được để trống")
        private String name;
        @NotBlank(message = "Giá trị biến thể không được để trống")
        private String value;

    }
}
