package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.statics.AppliedType;
import vn.techmaster.tranha.ecommerce.statics.DiscountType;
import vn.techmaster.tranha.ecommerce.statics.OwnerType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherSearchResponse {
    Long id;
    
    Long shopId;

    String shopName;

    String code;

    DiscountType discountType; //Loại giảm giá

    String voucherValue; // Giá trị giảm giá

    Double minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng mã

    LocalDateTime startDate;

    LocalDateTime endDate; //Ngày hết hạn của voucher.

    int usageLimit; //Giới hạn số lần sử dụng
}