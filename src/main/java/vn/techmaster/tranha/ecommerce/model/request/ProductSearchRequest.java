package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.statics.Gender;
import vn.techmaster.tranha.ecommerce.statics.Roles;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSearchRequest extends BaseSearchRequest {
    
    String name;

    Double minPrice;
    Double maxPrice;

    String prices;

    int stockQuantity;

    int averageRating;

    int soldQuantity;

    Category category;

    Shop shop;
}
