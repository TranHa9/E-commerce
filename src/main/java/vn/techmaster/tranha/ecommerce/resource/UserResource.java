package vn.techmaster.tranha.ecommerce.resource;

import vn.techmaster.tranha.ecommerce.exception.ExistedUserException;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.exception.PasswordNotMatchedException;
import vn.techmaster.tranha.ecommerce.model.request.CreateUserRequest;
import vn.techmaster.tranha.ecommerce.model.request.PasswordChangingRequest;
import vn.techmaster.tranha.ecommerce.model.request.UserSearchRequest;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.UserResponse;
import vn.techmaster.tranha.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserResource {

    UserService userService;

    @GetMapping
    public CommonSearchResponse<?> search(UserSearchRequest request) {
        return userService.searchUser(request);
    }

    @GetMapping("/{id}")
    public UserResponse getDetail(@PathVariable Long id) throws ObjectNotFoundException {
        return userService.getDetail(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateUserRequest request) throws ExistedUserException {
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(userResponse);
    }

}
