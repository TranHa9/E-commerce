package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.PaymentMethod;
import vn.techmaster.tranha.ecommerce.statics.PaymentStatus;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    Double amountDue; //Số tiền thanh toán

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;
}
