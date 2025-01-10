package vn.techmaster.tranha.ecommerce.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.statics.AppliedType;
import vn.techmaster.tranha.ecommerce.statics.DiscountType;
import vn.techmaster.tranha.ecommerce.statics.OwnerType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateVoucherRequest {

    Long shopId;

    String code;

    DiscountType discountType; //Loại giảm giá

    Double voucherValue; // Giá trị giảm giá

    Double minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng mã

    LocalDateTime startDate;

    LocalDateTime endDate; //Ngày hết hạn của voucher.

    int usageLimit; //Giới hạn số lần sử dụng
}
