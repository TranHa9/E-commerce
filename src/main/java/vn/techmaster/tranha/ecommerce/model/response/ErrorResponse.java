package vn.techmaster.tranha.ecommerce.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {

    String code;
    String message;

}
