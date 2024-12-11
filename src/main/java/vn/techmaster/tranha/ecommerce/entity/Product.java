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

    Double minPrice; // Giá thấp nhất cho sản phẩm (các biến thể khác nhau)
    Double maxPrice; // Giá cao nhất cho sản phẩm (các biến thể khác nhau)

    Double price;

    int stockQuantity;

    String origin; //Xuất xứ sản phẩm
    LocalDate expiryDate; //Hạn sử dụng

    int averageRating;
    int soldQuantity;

    String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;
}
