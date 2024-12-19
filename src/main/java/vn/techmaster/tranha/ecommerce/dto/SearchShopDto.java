package vn.techmaster.tranha.ecommerce.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.User;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchShopDto {
    Long id;
    String name;
    String description;
    String logo;
    Double rating;
    String userName;
    String email;
    Long userId;
    Long totalRecord;
}
