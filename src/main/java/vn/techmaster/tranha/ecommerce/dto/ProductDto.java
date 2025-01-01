package vn.techmaster.tranha.ecommerce.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    Long id;
    String name;
    List<CreateProductRequest.Prices> prices;
    List<String> imageUrls;
}
