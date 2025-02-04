package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressResponse {
    Long id;
    String address;
    String phone;
    boolean defaultAddress;
}
