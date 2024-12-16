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
    public ProductVariantResponse createProductVariant(MultipartFile image, CreateProductVariantRequest request) throws Exception {
        Optional<Product> productOptional = productRepository.findById(request.getProductId());
        if (productOptional.isEmpty()) {
            throw new Exception("Product not found");
        }
        Product product = productOptional.get();

        Optional<ProductAttribute> productAttributeOptional = productAttributeRepository.findById(request.getAttributeId());
        if (productAttributeOptional.isEmpty()) {
            throw new Exception("Product Attribute not found");
        }

        ProductAttribute productAttribute = productAttributeOptional.get();

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = saveProductImage(image);
        }
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .productAttribute(productAttribute)
                .variantValue(request.getVariantValue())
                .stockQuantity(request.getStockQuantity())
                .price(request.getPrice())
                .image(imagePath)
                .status(request.getStatus())
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

        double minPrice = product.getPrice();
        double maxPrice = product.getPrice();
        int totalStock = 0;

        for (ProductVariant variant : variants) {
            double variantPrice = product.getPrice();
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
