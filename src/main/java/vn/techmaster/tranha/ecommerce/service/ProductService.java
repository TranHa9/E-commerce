package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.dto.SearchProductDto;
import vn.techmaster.tranha.ecommerce.entity.*;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.request.ProductSearchRequest;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductSearchResponse;
import vn.techmaster.tranha.ecommerce.repository.*;
import vn.techmaster.tranha.ecommerce.repository.custom.ProductCustomRepository;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ShopRepository shopRepository;
    ProductCustomRepository productCustomRepository;
    ProductVariantRepository productVariantRepository;
    ProductAttributeRepository productAttributeRepository;
    ObjectMapper objectMapper;


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

    @Transactional(rollbackFor = Exception.class)
    public ProductResponse createProduct(CreateProductRequest request) throws Exception {
        Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
        Optional<Shop> shopOptional = shopRepository.findById(request.getShopId());

        if (categoryOptional.isEmpty()) {
            throw new Exception("Category not found");
        }
        if (shopOptional.isEmpty()) {
            throw new Exception("Shop not found");
        }

        //Chuyển đổi thành json để lưu
        String imageUrlsJson = objectMapper.writeValueAsString(request.getImageUrls());
        String pricesJson = objectMapper.writeValueAsString(request.getPrices());

        // Tính tổng stockQuantity, minPrice, maxPrice
        List<CreateProductRequest.Prices> variants = objectMapper.readValue(pricesJson, new TypeReference<List<CreateProductRequest.Prices>>() {
        });
        int totalStockQuantity = 0;
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;

        for (CreateProductRequest.Prices variant : variants) {
            totalStockQuantity += variant.getStockQuantity();
            if (variant.getPrice() < minPrice) {
                minPrice = variant.getPrice();
            }
            if (variant.getPrice() > maxPrice) {
                maxPrice = variant.getPrice();
            }
        }
        // Tạo sản phẩm chính
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .prices(pricesJson)
                .stockQuantity(totalStockQuantity)
                .origin(request.getOrigin())
                .brand(request.getBrand())
                .expiryDate(request.getExpiryDate())
                .category(categoryOptional.get())
                .shop(shopOptional.get())
                .imageUrls(imageUrlsJson)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        productRepository.save(product);
        // Tạo danh sách các productVariants và productAttributes
        List<ProductVariant> savedVariants = new ArrayList<>();
        List<ProductAttribute> savedAttributes = new ArrayList<>();

        for (ProductVariant variant : request.getVariants()) {
            variant.setProduct(product);
            variant.setStatus(VariantStatus.ACTIVE);
            savedVariants.add(variant);
            for (ProductAttribute attribute : variant.getAttributes()) {
                attribute.setProductVariant(variant);
                savedAttributes.add(attribute);
            }
        }
        productVariantRepository.saveAll(savedVariants);
        productAttributeRepository.saveAll(savedAttributes);

        List<CreateProductRequest.Prices> productVariants = objectMapper.readValue(product.getPrices(), new TypeReference<List<CreateProductRequest.Prices>>() {
        });
        List<String> imageUrls = objectMapper.readValue(product.getImageUrls(), new TypeReference<List<String>>() {
        });
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .minPrice(product.getMinPrice())
                .maxPrice(product.getMaxPrice())
                .prices(productVariants)
                .imageUrls(imageUrls)
                .stockQuantity(product.getStockQuantity())
                .origin(product.getOrigin())
                .brand(product.getBrand())
                .expiryDate(product.getExpiryDate())
                .category(product.getCategory())
                .shop(product.getShop())
                .build();
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
