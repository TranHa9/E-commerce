package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.AppliedType;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "voucher_conditions")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherCondition extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    @Enumerated(EnumType.STRING)
    AppliedType appliedType;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    Category category; // Danh mục áp dụng (nếu có)

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    Product product; // Sản phẩm áp dụng (nếu có)
}
