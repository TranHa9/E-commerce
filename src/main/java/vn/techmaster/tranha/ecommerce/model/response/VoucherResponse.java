package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.statics.DiscountType;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {
    Long id;

    Shop shop;

    String code;

    DiscountType discountType; //Loại giảm giá

    Double voucherValue; // Giá trị giảm giá

    Double minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng mã

    LocalDateTime startDate;

    LocalDateTime endDate; //Ngày hết hạn của voucher.

    int usageLimit; //Giới hạn số lần sử dụng
}
