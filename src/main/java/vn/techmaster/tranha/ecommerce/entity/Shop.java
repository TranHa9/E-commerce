package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shops")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shop extends BaseEntity {

    String name;

    String description;

    int rating;

    String logo;

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;
}
