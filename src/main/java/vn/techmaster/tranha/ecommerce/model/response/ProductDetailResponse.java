package vn.techmaster.tranha.ecommerce.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.dto.ProductVariantDto;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
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
    String shopName;
    List<ProductVariantDto> variants;
}
