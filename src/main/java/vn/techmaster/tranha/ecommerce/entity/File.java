package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.FileType;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File extends BaseEntity {

    String name;

    String extension;

    @Enumerated(EnumType.STRING)
    FileType type;

    String url;

    int size;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    Product product;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = true)
    Shop shop;

    @ManyToOne
    @JoinColumn(name = "rating_product_id", nullable = true)
    RatingProduct ratingProduct;

    @ManyToOne
    @JoinColumn(name = "rating_shop_id", nullable = true)
    RatingShop ratingShop;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = true)
    Message message;
}
