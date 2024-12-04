package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 16, message = "Mật khẩu phải có từ 6 đến 16 ký tự")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{6,16}$",
            message = "Mật khẩu phải chứa cả chữ cái và số")
    String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 ký tự, bắt đầu bằng số 0 và chỉ chứa chữ số")
    String phone;

}
