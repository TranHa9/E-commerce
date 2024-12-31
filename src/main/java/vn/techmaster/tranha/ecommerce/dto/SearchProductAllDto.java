package vn.techmaster.tranha.ecommerce.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchProductAllDto {
    Long id;
    Long categoryId;
    String productName;
    Double averageRating;
    String description;
    List<String> productImages;
    Double maxPrice;
    Double minPrice;
    List<CreateProductRequest.Prices> prices;
    String origin;
    String brand;
    String expiryDate;
    Integer productStockQuantity;
    Integer soldQuantity;
    String categoryName;
    Long shopId;
    String shopName;
    String status;
    List<ProductVariantDto> variants;
    Long totalRecord;

}
