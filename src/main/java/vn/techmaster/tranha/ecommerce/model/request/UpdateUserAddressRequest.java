package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserAddressRequest {
    @NotBlank(message = "Address không được để trống")
    String address;

    @NotBlank(message = "Phone không được để trống")
    String phone;

}
