package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

import java.time.LocalDate;
import java.util.List;

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

    @Lob
    String prices; // json bao gồm giá,số lượng, variants

    int stockQuantity;

    String origin; //Xuất xứ sản phẩm

    String brand; //thương hiệu

    LocalDate expiryDate; //Hạn sử dụng

    @Column(name = "average_rating", columnDefinition = "DOUBLE DEFAULT 0")
    Double averageRating;

    @Column(name = "sold_quantity", columnDefinition = "INT DEFAULT 0")
    int soldQuantity;

    String imageUrls;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @Enumerated(EnumType.STRING)
    ProductStatus status;
}
