package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserAddressRequest {

    @NotBlank(message = "Tên không được để trống")
    String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 ký tự, bắt đầu bằng số 0 và chỉ chứa chữ số")
    String phone;

    @NotNull(message = "userId không được để trống")
    Long userId;
}
