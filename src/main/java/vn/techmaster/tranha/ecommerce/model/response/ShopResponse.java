package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopResponse {
    Long id;
    String name;
    String description;
    Double rating;
    String logo;
    String email;
    String phone;
    String address;
    UserResponse user;
}
