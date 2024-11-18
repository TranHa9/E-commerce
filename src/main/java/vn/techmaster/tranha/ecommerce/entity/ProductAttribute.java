package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_attributes")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttribute extends BaseEntity {

    String name;
    
    String description;
}
