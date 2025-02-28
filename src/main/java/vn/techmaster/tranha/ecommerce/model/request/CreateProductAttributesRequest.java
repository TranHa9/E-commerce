package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductAttributesRequest {

    Long variantId;

    @NotBlank(message = "Tên thuộc tính không được để trống")
    @Size(max = 150, message = "Tên thuộc tính không được vượt quá 150 ký tự")
    String name;

    @NotBlank(message = "Gía trị thuộc tính không được để trống")
    @Size(max = 150, message = "Gía trị thuộc tính không được vượt quá 150 ký tự")
    String value;
}
