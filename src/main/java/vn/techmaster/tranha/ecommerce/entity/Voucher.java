package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.AppliedType;
import vn.techmaster.tranha.ecommerce.statics.DiscountType;
import vn.techmaster.tranha.ecommerce.statics.OwnerType;

import java.time.LocalDateTime;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vouchers")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @Column(nullable = false, unique = true)
    String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    DiscountType discountType;

    String voucherValue; // Giá trị giảm giá

    @Column(name = "min_order_value")
    Double minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng mã

    @Enumerated(EnumType.STRING)
    @Column(name = "applied_type")
    AppliedType appliedType;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type")
    OwnerType ownerType;

    @Column(name = "end_date")
    LocalDateTime endDate;

    int usageLimit;
}
