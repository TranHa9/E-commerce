package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.techmaster.tranha.ecommerce.dto.SearchUserDto;
import vn.techmaster.tranha.ecommerce.entity.Role;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.exception.ExistedUserException;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CreateUserRequest;
import vn.techmaster.tranha.ecommerce.model.request.UserSearchRequest;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.UserResponse;
import vn.techmaster.tranha.ecommerce.model.response.UserSearchResponse;
import vn.techmaster.tranha.ecommerce.repository.RoleRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.UserCustomRepository;
import vn.techmaster.tranha.ecommerce.statics.Roles;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {

    final PasswordEncoder passwordEncoder;

    final UserRepository userRepository;

    final RoleRepository roleRepository;

    final ObjectMapper objectMapper;

    final UserCustomRepository userCustomRepository;

    public UserResponse getDetail(Long id) throws ObjectNotFoundException {
        return userRepository.findById(id)
                .map(u -> objectMapper.convertValue(u, UserResponse.class))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }


    public UserResponse createUser(CreateUserRequest request) throws ExistedUserException {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            throw new ExistedUserException("Username existed");
        }

        Set<Role> roles = roleRepository.findByName(Roles.USER).stream().collect(Collectors.toSet());

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode("123")) // TODO: change to random password
                .roles(roles)
                .status(UserStatus.ACTIVATED)
                .build();
        userRepository.save(user);
        return objectMapper.convertValue(user, UserResponse.class);
    }

    public CommonSearchResponse<UserSearchResponse> searchUser(UserSearchRequest request) {
        List<SearchUserDto> result = userCustomRepository.searchUser(request);

        Long totalRecord = 0L;
        List<UserSearchResponse> studentResponses = new ArrayList<>();
        if (!result.isEmpty()) {
            totalRecord = result.get(0).getTotalRecord();
            studentResponses = result
                    .stream()
                    .map(s -> objectMapper.convertValue(s, UserSearchResponse.class))
                    .toList();
        }

        int totalPage = (int) Math.ceil((double) totalRecord / request.getPageSize());

        return CommonSearchResponse.<UserSearchResponse>builder()
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .data(studentResponses)
                .pageInfo(new CommonSearchResponse.CommonPagingResponse(request.getPageSize(), request.getPageIndex()))
                .build();
    }

}
