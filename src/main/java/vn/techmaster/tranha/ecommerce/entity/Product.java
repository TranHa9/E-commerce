package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    String name;
    String description;
    Double minPrice;
    Double maxPrice;

    @Column(length = 5000)
    String prices;

    String images;

    int stockQuantity;
    String origin; //Xuất xứ sản phẩm
    LocalDate expiryDate; //Hạn sử dụng
    int averageRating;
    int soldQuantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;
}
