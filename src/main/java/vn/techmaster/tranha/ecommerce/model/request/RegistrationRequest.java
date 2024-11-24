package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

    @NotBlank(message = "fullName cannot be blank")
    String fullName;

    @NotBlank(message = "User cannot be blank")
    @Email
    String username;

    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotBlank(message = "Phone cannot be blank")
    String phone;

}
