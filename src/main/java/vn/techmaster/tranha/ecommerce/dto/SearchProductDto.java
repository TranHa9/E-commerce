package vn.techmaster.tranha.ecommerce.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchProductDto {
    Long id;
    String productName;
    Double productPrice;
    Double averageRating;
    String description;
    String productImages;
    Double maxPrice;
    Double minPrice;
    String origin;
    String expiryDate;
    Integer productStockQuantity;
    Integer soldQuantity;
    String categoryName;
    String shopName;
    List<ProductVariantDto> variants;
    Long totalRecord;

}
