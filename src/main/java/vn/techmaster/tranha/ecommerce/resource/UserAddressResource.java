package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CreateUserAddressRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateUserAddressRequest;
import vn.techmaster.tranha.ecommerce.model.response.UserAddressResponse;
import vn.techmaster.tranha.ecommerce.service.UserAddressService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-address")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAddressResource {
    UserAddressService userAddressService;

    @GetMapping
    public List<UserAddressResponse> getAddressAll() {
        return userAddressService.getAddressAll();
    }

    @GetMapping("/{id}")
    public UserAddressResponse getUserAddressById(@PathVariable Long id) throws ObjectNotFoundException {
        return userAddressService.getUserAddressById(id);
    }

    @PostMapping
    public UserAddressResponse createAddress(@RequestBody @Valid CreateUserAddressRequest request) {
        return userAddressService.create(request);
    }

    @PutMapping("/{id}")
    public UserAddressResponse updateUserAddress(@PathVariable Long id, @Valid @RequestBody UpdateUserAddressRequest request) {
        return userAddressService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUserAddress(@PathVariable Long id) {
        userAddressService.delete(id);
    }

    @PatchMapping("/{id}/set-default")
    public UserAddressResponse setDefaultAddress(@PathVariable Long id) throws ObjectNotFoundException {
        return userAddressService.updateDefaultAddress(id);
    }
}
