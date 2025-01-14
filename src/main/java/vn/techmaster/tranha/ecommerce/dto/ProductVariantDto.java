package vn.techmaster.tranha.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantDto {
    Long id;

    String image;

    Double price;

    @JsonProperty("stock_quantity")
    Integer stockQuantity;

    List<ProductAttributesDto> attributes;

    String status;
}
