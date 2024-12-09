package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.techmaster.tranha.ecommerce.statics.Gender;
import vn.techmaster.tranha.ecommerce.statics.Roles;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;

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

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)?$", message = "Giới tính không hợp lệ, phải là MALE, FEMALE hoặc OTHER")
    Gender gender;

    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 ký tự, bắt đầu bằng số 0 và chỉ chứa chữ số")
    String phone;

    @Pattern(regexp = "^(USER|ADMIN|SHOP)?$", message = "Vai trò không hợp lệ phải là USER, ADMIN, SHOP")
    Roles role;

    @Pattern(regexp = "^(CREATED|ACTIVATED|DEACTIVATED|LOCKED)?$", message = "Trạng thái không hợp lệ phải là CREATED, ACTIVATED, DEACTIVATED và LOCKED")
    UserStatus status;


}
