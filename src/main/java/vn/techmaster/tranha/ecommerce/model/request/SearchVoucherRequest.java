package vn.techmaster.tranha.ecommerce.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchVoucherRequest extends BaseSearchRequest {

    String searchCode;

    String searchDiscountType; //Loại giảm giá

    String searchVoucherValue; // Giá trị giảm giá
}
