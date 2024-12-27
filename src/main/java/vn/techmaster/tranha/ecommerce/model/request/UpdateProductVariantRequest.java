package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductVariantRequest {

    @NotNull(message = "Gía của biến thể không được để trống")
    @Min(value = 1000, message = "Giá phải lớn hơn hoặc bằng 1000")
    Double price;

    @Min(value = 0, message = "Số lượng tồn kho phải là một số không âm")
    int stockQuantity;

    String imageUrl;

    @NotEmpty(message = "Danh sách thuộc tính không được để trống")
    @Valid
    List<UpdateProductAttributeRequest> attributes;

    @NotNull(message = "Trạng thái không được để trống")
    VariantStatus status;
}
