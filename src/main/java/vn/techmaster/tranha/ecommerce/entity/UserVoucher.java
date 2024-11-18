package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_vouchers")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVoucher extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    boolean isUsed; // Voucher đã sử dụng hay chưa

    int usageCount; // Số lần voucher đã được sử dụng

    LocalDateTime usedAt; // Thời gian voucher được sử dụng
}
