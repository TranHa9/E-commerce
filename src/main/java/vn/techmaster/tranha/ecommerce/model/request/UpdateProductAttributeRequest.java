package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductAttributeRequest {

    @NotBlank(message = "Tên thuộc tính không được để trống")
    @Size(max = 150, message = "Tên thuộc tính không được vượt quá 150 ký tự")
    String name;

    @NotBlank(message = "Gía trị thuộc tính không được để trống")
    @Size(max = 150, message = "Gía trị thuộc tính không được vượt quá 150 ký tự")
    String value;
}
