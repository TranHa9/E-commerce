package vn.techmaster.tranha.ecommerce.model.response;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.entity.Shop;

import java.time.LocalDate;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    Long id;

    String name;

    String description;

    Double minPrice;

    Double maxPrice;

    String basePrice;

    String imageUrls;

    int stockQuantity;

    String origin; //Xuất xứ sản phẩm

    String brand;

    LocalDate expiryDate; //Hạn sử dụng

    Double averageRating;

    int soldQuantity;

    Category category;

    Shop shop;
}
