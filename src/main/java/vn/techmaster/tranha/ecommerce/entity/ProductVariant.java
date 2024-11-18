package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

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

    @ManyToOne
    @JoinColumn(name = "product_attribute_id")
    ProductAttribute productAttribute;

    String variantValue;

    //Double price;

    int stockQuantity;

    String image;

    @Enumerated(EnumType.STRING)
    VariantStatus status;
}
