package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

    @NotBlank(message = "User cannot be blank")
    @Length(max = 50, message = "username must be less than 50 characters")
    String username;

    @NotBlank(message = "Password cannot be blank")
    String password;

}
