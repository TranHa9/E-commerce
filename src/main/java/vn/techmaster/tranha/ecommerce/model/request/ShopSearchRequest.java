package vn.techmaster.tranha.ecommerce.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopSearchRequest extends BaseSearchRequest {

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    String name;

    Double rattingStart;
    Double rattingEnd;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    String email;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    String userName;


}
