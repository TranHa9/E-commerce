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

    @ManyToOne
    @JoinColumn(name = "product_attribute_id")
    ProductAttribute productAttribute;

    @JsonProperty("variant_value")
    String variantValue;

    @JsonProperty("price")
    Double price;

    @JsonProperty("stock_quantity")
    int stockQuantity;

    @JsonProperty("image")
    String image;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    VariantStatus status;
}
