package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.tranha.ecommerce.entity.Product;
import vn.techmaster.tranha.ecommerce.entity.ProductAttribute;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductVariantRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductVariantResponse;
import vn.techmaster.tranha.ecommerce.repository.ProductAttributeRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductVariantRepository;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {

    ProductRepository productRepository;
    ProductAttributeRepository productAttributeRepository;
    ProductVariantRepository productVariantRepository;
    ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public ProductVariantResponse createProductVariant(MultipartFile[] images, CreateProductVariantRequest request) throws Exception {
        Optional<Product> productOptional = productRepository.findById(request.getProductId());
        if (productOptional.isEmpty()) {
            throw new Exception("Product not found");
        }
        Product product = productOptional.get();

        List<String> imagePaths = new ArrayList<>();
        if (images != null && images.length > 0) {
            // Lưu tất cả ảnh
            for (MultipartFile image : images) {
                String imagePath = saveProductImage(image);
                imagePaths.add(imagePath);
            }
        }
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .stockQuantity(request.getStockQuantity())
                .price(request.getPrice())
                .imageUrls(imagePaths.toString())
                .status(VariantStatus.ACTIVE)
                .build();
        productVariantRepository.save(productVariant);


        updateProductDetails(product);

        return objectMapper.convertValue(productVariant, ProductVariantResponse.class);
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

    public void updateProductDetails(Product product) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

        double minPrice = product.getBasePrice();
        double maxPrice = product.getBasePrice();
        int totalStock = 0;

        for (ProductVariant variant : variants) {
            double variantPrice = variant.getPrice();
            if (variantPrice < minPrice) {
                minPrice = variantPrice;
            }
            if (variantPrice > maxPrice) {
                maxPrice = variantPrice;
            }
            totalStock += variant.getStockQuantity();
        }

        product.setMinPrice(minPrice);
        product.setMaxPrice(maxPrice);
        product.setStockQuantity(totalStock);

        productRepository.save(product);
    }
}
