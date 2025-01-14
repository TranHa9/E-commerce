package vn.techmaster.tranha.ecommerce.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.dto.ProductVariantDto;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

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
    ProductStatus status;
    List<ProductVariantDto> variants;
}
