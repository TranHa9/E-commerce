package vn.techmaster.tranha.ecommerce.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.dto.ProductVariantDto;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSearchResponse {
    Long id;
    String productName;
    Double productPrice;
    Double averageRating;
    String description;
    String productImage;
    Double maxPrice;
    Double minPrice;
    String origin;
    String expiryDate;
    Integer productStockQuantity;
    Integer soldQuantity;
    String categoryName;
    String shopName;
    List<ProductVariantDto> variants;
}
