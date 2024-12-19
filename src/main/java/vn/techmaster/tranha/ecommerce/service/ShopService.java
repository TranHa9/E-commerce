package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.dto.SearchShopDto;
import vn.techmaster.tranha.ecommerce.entity.Role;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.ShopCreateRequest;
import vn.techmaster.tranha.ecommerce.model.request.ShopSearchRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateShopRequest;
import vn.techmaster.tranha.ecommerce.model.response.*;
import vn.techmaster.tranha.ecommerce.repository.RoleRepository;
import vn.techmaster.tranha.ecommerce.repository.ShopRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.ShopCustomRepository;
import vn.techmaster.tranha.ecommerce.statics.Roles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    ShopRepository shopRepository;
    ShopCustomRepository shopCustomRepository;
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
                .rating(0.0)
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

    public ShopResponse findByShopId(Long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        return objectMapper.convertValue(shop, ShopResponse.class);
    }

    public CommonSearchResponse<?> searchShop(ShopSearchRequest request) {
        List<SearchShopDto> result = shopCustomRepository.searchShop(request);
        Long totalRecord = 0L;
        List<ShopSearchResponse> shopResponses = new ArrayList<>();
        if (!result.isEmpty()) {
            totalRecord = result.get(0).getTotalRecord();
            shopResponses = result
                    .stream()
                    .map(s -> objectMapper.convertValue(s, ShopSearchResponse.class))
                    .toList();
        }

        int totalPage = (int) Math.ceil((double) totalRecord / request.getPageSize());

        return CommonSearchResponse.<ShopSearchResponse>builder()
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .data(shopResponses)
                .pageInfo(new CommonSearchResponse.CommonPagingResponse(request.getPageSize(), request.getPageIndex()))
                .build();
    }

    public ShopResponse updateShop(Long id, MultipartFile logo, UpdateShopRequest request) throws ObjectNotFoundException, IOException {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Shop not found"));
        shop.setName(request.getName());
        shop.setDescription(request.getDescription());
        if (logo != null && !logo.isEmpty()) {
            String fileName = saveLogo(logo);
            shop.setLogo(fileName);
        }
        shopRepository.save(shop);
        return objectMapper.convertValue(shop, ShopResponse.class);
    }

    private String saveLogo(MultipartFile logo) throws IOException {
        String uploadDir = "images/logo" + File.separator;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + File.separator + fileName);
        try {
            Files.copy(logo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new IOException("Could not save logo image", e);
        }
    }
}
