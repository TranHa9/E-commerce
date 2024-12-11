package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.entity.Product;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.model.response.UserResponse;
import vn.techmaster.tranha.ecommerce.repository.CategoryRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductRepository;
import vn.techmaster.tranha.ecommerce.repository.ShopRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ShopRepository shopRepository;
    ObjectMapper objectMapper;

    public ProductResponse createProduct(MultipartFile image, CreateProductRequest request) throws Exception {
        Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
        Optional<Shop> shopOptional = shopRepository.findById(request.getShopId());

        if (categoryOptional.isEmpty()) {
            throw new Exception("Category not found");
        }
        if (shopOptional.isEmpty()) {
            throw new Exception("Shop not found");
        }

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = saveProductImage(image);
        }
        // Tạo sản phẩm
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .origin(request.getOrigin())
                .expiryDate(request.getExpiryDate())
                .category(categoryOptional.get())
                .shop(shopOptional.get())
                .image(imagePath)
                .build();

        productRepository.save(product);
        return objectMapper.convertValue(product, ProductResponse.class);
    }

    private String saveProductImage(MultipartFile avatar) throws IOException {
        String uploadDir = "images/product" + File.separator;

        File dir = new File(uploadDir);
        // Kiểm tra nếu thư mục không tồn tại thì tạo mới
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + File.separator + fileName);
        try {
            Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new IOException("Could not save avatar image", e);
        }
    }
}
