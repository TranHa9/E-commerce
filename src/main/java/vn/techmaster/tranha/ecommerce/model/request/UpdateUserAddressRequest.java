package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserAddressRequest {
    @NotBlank(message = "Tên không được để trống")
    String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    String address;

    @NotBlank(message = "Điện thoại không được để trống")
    String phone;

}
