package vn.techmaster.tranha.ecommerce.model.response;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    Long id;

    String name;

    String description;

    Double minPrice;

    Double maxPrice;

    List<CreateProductRequest.Prices> prices;

    List<String> imageUrls;

    int stockQuantity;

    String origin; //Xuất xứ sản phẩm

    String brand;

    LocalDate expiryDate; //Hạn sử dụng

    Double averageRating;

    int soldQuantity;

    Category category;

    Shop shop;

}
