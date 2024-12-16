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
    float rating;
    String logo;
    String email;
    String phone;
}
