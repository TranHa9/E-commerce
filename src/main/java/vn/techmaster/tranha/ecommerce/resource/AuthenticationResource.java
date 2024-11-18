package vn.techmaster.tranha.ecommerce.resource;

import vn.techmaster.tranha.ecommerce.exception.ExistedUserException;
import vn.techmaster.tranha.ecommerce.exception.InvalidRefreshTokenException;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.LoginRequest;
import vn.techmaster.tranha.ecommerce.model.request.RefreshTokenRequest;
import vn.techmaster.tranha.ecommerce.model.request.RegistrationRequest;
import vn.techmaster.tranha.ecommerce.model.response.JwtResponse;
import vn.techmaster.tranha.ecommerce.model.response.UserResponse;
import vn.techmaster.tranha.ecommerce.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/authentications")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationResource {

    AuthenticationService authenticateService;

    @PostMapping("/login")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest request) throws ObjectNotFoundException {
        return authenticateService.authenticate(request);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegistrationRequest request)
            throws ExistedUserException, ObjectNotFoundException, MessagingException {
        UserResponse userResponse = authenticateService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/refresh_token")
    public JwtResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request)
            throws InvalidRefreshTokenException {
        return authenticateService.refreshToken(request);
    }

    @PostMapping("/logout")
    public void logout() {
        authenticateService.logout();
    }

}
