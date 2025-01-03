package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductVariantRequest {

    Long productId;

    @NotNull(message = "Danh sách giá không được để trống")
    @Min(value = 1000, message = "Giá phải lớn hơn hoặc bằng 1000")
    Double price;

    @Min(value = 0, message = "Số lượng tồn kho phải là một số không âm")
    int stockQuantity;

    String imageUrl;

    @NotEmpty(message = "Danh sách thuộc tính không được để trống")
    @Valid
    List<CreateProductAttributesRequest> attributes;
}