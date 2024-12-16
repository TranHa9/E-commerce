package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techmaster.tranha.ecommerce.entity.Role;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.model.request.ShopCreateRequest;
import vn.techmaster.tranha.ecommerce.model.response.ShopResponse;
import vn.techmaster.tranha.ecommerce.repository.RoleRepository;
import vn.techmaster.tranha.ecommerce.repository.ShopRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.statics.Roles;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    ShopRepository shopRepository;
    ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> createShop(Long id, @Valid ShopCreateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isShop = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(Roles.SHOP));

        if (isShop) {
            return ResponseEntity.badRequest().body("User is already a shop");
        }
        Set<Role> roles = roleRepository.findByName(Roles.SHOP).stream().collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        Shop shop = Shop.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .rating(0)
                .user(user)
                .build();
        shopRepository.save(shop);

        ShopResponse response = ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .description(shop.getDescription())
                .rating(shop.getRating())
                .email(user.getEmail())
                .phone(user.getPhone())
                .logo(shop.getLogo())
                .build();

        return ResponseEntity.ok(response);
    }

    public ShopResponse findByUserId(Long userId) {
        Shop shop = shopRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return objectMapper.convertValue(shop, ShopResponse.class);
    }
}
