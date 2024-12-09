package vn.techmaster.tranha.ecommerce.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.statics.Gender;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSearchResponse {

    Long id;

    String name;

    String email;

    String phone;

    Gender gender;

    LocalDate dob;

    String avatar;

    String role;

    String status;


}
