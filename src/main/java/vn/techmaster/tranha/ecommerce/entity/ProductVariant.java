package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

import java.util.List;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variants")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    Double price;

    int stockQuantity;

    String imageUrl;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> attributes;

    @Enumerated(EnumType.STRING)
    ProductStatus status;
}
