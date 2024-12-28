package vn.techmaster.tranha.ecommerce.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategorySearchRequest extends BaseSearchRequest {
    String name;
    String status;
}
