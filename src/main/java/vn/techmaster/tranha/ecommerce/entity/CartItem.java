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
@Table(name = "cart_items")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cart_id")
    Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    String variants;

    int quantity;

    Double unitPrice;

    Double totalPrice;
}
