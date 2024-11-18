package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_item_vouchers")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemVoucher extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    Double discountAmount;
}
