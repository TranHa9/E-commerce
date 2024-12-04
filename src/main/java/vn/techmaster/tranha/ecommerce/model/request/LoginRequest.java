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
public class LoginRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 16, message = "Mật khẩu phải có từ 6 đến 16 ký tự")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{6,16}$",
            message = "Mật khẩu phải chứa cả chữ cái và số")
    String password;

}
