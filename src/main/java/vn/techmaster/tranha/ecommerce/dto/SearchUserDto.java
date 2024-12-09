package vn.techmaster.tranha.ecommerce.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchUserDto {

    Long id;
    String name;
    String email;
    String phone;
    String dob;
    String gender;
    String status;
    String avatar;
    String role;
    Long totalRecord;
}
