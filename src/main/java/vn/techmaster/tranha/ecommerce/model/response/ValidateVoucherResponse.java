package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidateVoucherResponse {
    boolean isValid;
    String message;
    Double discountValue;
    VoucherResponse voucher;
}
