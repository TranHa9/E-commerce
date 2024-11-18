package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.OrderStatus;
import vn.techmaster.tranha.ecommerce.statics.PaymentStatus;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "user_address_id")
    UserAddress userAddress;

    Double totalAmount;

    Double discountApplied; //Số tiền giảm giá

    Double finalAmount; //Số tiền thanh toán cuối cùng

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;
}
