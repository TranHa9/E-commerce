package vn.techmaster.tranha.ecommerce.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopSearchResponse {
    Long id;
    String name;
    String description;
    String logo;
    Double rating;
    String userName;
    String email;
    Long userId;
}
