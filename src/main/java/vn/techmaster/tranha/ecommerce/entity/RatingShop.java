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
@Table(name = "rating_shops")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingShop extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    int rating;

    String comment;

    String mediaUrl;
}
