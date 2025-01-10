package vn.techmaster.tranha.ecommerce.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopSearchRequest extends BaseSearchRequest {
    String name;

    Double rattingStart;
    Double rattingEnd;
    String email;
    String userName;
}
