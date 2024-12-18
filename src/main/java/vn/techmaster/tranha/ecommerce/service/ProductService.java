package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.dto.SearchProductDto;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.entity.Product;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.request.ProductSearchRequest;
import vn.techmaster.tranha.ecommerce.model.response.*;
import vn.techmaster.tranha.ecommerce.repository.CategoryRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductRepository;
import vn.techmaster.tranha.ecommerce.repository.ShopRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.ProductCustomRepository;

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
    ProductCustomRepository productCustomRepository;
    ObjectMapper objectMapper;

    public ProductResponse createProduct(MultipartFile[] images, CreateProductRequest request) throws Exception {
        Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
        Optional<Shop> shopOptional = shopRepository.findById(request.getShopId());

        if (categoryOptional.isEmpty()) {
            throw new Exception("Category not found");
        }
        if (shopOptional.isEmpty()) {
            throw new Exception("Shop not found");
        }

        List<String> imagePaths = new ArrayList<>();
        if (images != null && images.length > 0) {
            // Lưu tất cả ảnh
            for (MultipartFile image : images) {
                String imagePath = saveProductImage(image);
                imagePaths.add(imagePath);
            }
        }
        // Tạo sản phẩm
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .stockQuantity(request.getStockQuantity())
                .origin(request.getOrigin())
                .brand(request.getBrand())
                .expiryDate(request.getExpiryDate())
                .category(categoryOptional.get())
                .shop(shopOptional.get())
                .imageUrls(imagePaths.toString())
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

    public CommonSearchResponse<?> searchProduct(ProductSearchRequest request) {
        List<SearchProductDto> result = productCustomRepository.searchProduct(request);
        Long totalRecord = 0L;
        List<ProductSearchResponse> productResponses = new ArrayList<>();
        if (!result.isEmpty()) {
            totalRecord = result.get(0).getTotalRecord();
            productResponses = result
                    .stream()
                    .map(s -> objectMapper.convertValue(s, ProductSearchResponse.class))
                    .toList();
        }
        int totalPage = (int) Math.ceil((double) totalRecord / request.getPageSize());

        return CommonSearchResponse.<ProductSearchResponse>builder()
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .data(productResponses)
                .pageInfo(new CommonSearchResponse.CommonPagingResponse(request.getPageSize(), request.getPageIndex()))
                .build();
    }
}
