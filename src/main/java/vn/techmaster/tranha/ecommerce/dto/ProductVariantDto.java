package vn.techmaster.tranha.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantDto {
    Long id;
    @JsonProperty("attribute_name")
    String attributeName;
    @JsonProperty("variant_value")
    String variantValue;
    String image;
    Double price;
    @JsonProperty("stock_quantity")
    Integer stockQuantity;
    String status;
}
