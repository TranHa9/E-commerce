package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Role;
import vn.techmaster.tranha.ecommerce.statics.Gender;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    String email;
    
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 ký tự, bắt đầu bằng số 0 và chỉ chứa chữ số")
    String phone;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)?$", message = "Giới tính không hợp lệ, phải là MALE, FEMALE hoặc OTHER")
    Gender gender;

    @PastOrPresent(message = "Ngày sinh phải là ngày trong quá khứ hoặc ngày hôm nay")
    LocalDate dob;
}
