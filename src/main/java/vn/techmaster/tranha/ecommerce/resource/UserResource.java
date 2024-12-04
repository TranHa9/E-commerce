package vn.techmaster.tranha.ecommerce.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.exception.ExistedUserException;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CreateUserRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateUserRequest;
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
import vn.techmaster.tranha.ecommerce.statics.Gender;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserResource {

    UserService userService;

    ObjectMapper objectMapper;

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

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestPart("request") String updateUserRequest,
                                   @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws ObjectNotFoundException, IOException {
        try {
            UpdateUserRequest request = objectMapper.readValue(updateUserRequest, UpdateUserRequest.class);
            return userService.updateUser(id, avatar, request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ", e);
        }
    }

}
