package vn.techmaster.tranha.ecommerce.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductAttrbutesRequest {
    String name;
    String description;
}
