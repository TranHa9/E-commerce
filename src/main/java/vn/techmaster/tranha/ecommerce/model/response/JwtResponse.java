package vn.techmaster.tranha.ecommerce.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtResponse {

    String jwt;

    String refreshToken;

    Long id;

    String email;

    String name;

    Set<String> roles;

}
