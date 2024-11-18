package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_address")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddress extends BaseEntity {

    String address;
    String street;

    @Column(columnDefinition = "boolean default false")
    boolean defaultAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
