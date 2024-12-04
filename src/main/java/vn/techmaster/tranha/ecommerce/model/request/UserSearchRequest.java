package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserSearchRequest extends BaseSearchRequest {

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    String email;

    Boolean activated;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)?$", message = "Giới tính không hợp lệ, phải là MALE, FEMALE hoặc OTHER")
    String gender;

}
