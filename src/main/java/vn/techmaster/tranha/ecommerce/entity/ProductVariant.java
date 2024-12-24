package vn.techmaster.tranha.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    String attributes;

    int stockQuantity;

    String imageUrls;

    @Enumerated(EnumType.STRING)
    VariantStatus status;
}
