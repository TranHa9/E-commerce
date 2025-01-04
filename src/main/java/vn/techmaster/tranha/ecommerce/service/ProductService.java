package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techmaster.tranha.ecommerce.dto.SearchProductDto;
import vn.techmaster.tranha.ecommerce.entity.*;
import vn.techmaster.tranha.ecommerce.model.request.*;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductDetailResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductSearchResponse;
import vn.techmaster.tranha.ecommerce.repository.*;
import vn.techmaster.tranha.ecommerce.repository.custom.ProductCustomRepository;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

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
    CartItemRepository cartItemRepository;
    ObjectMapper objectMapper;


    public CommonSearchResponse<?> searchProducts(ProductSearchRequest request) {
        List<SearchProductDto> result = productCustomRepository.searchProducts(request);
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
                .status(ProductStatus.ACTIVE)
                .build();

        productRepository.save(product);
        // Tạo danh sách các productVariants và productAttributes
        List<ProductVariant> savedVariants = new ArrayList<>();
        List<ProductAttribute> savedAttributes = new ArrayList<>();

        for (CreateProductVariantRequest variantRequest : request.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setPrice(variantRequest.getPrice());
            variant.setStockQuantity(variantRequest.getStockQuantity());
            variant.setImageUrl(variantRequest.getImageUrl());
            variant.setStatus(ProductStatus.ACTIVE);
            variant.setProduct(product);
            savedVariants.add(variant);
            if (variantRequest.getAttributes() != null) {
                for (CreateProductAttributesRequest attributeRequest : variantRequest.getAttributes()) {
                    ProductAttribute attribute = new ProductAttribute();
                    attribute.setName(attributeRequest.getName());
                    attribute.setValue(attributeRequest.getValue());
                    attribute.setProductVariant(variant);
                    savedAttributes.add(attribute);
                }
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
                .status(product.getStatus())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) throws Exception {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new Exception("Product not found");
        }
        Product product = productOptional.get();
        Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new Exception("Category not found");
        }

        String imageUrlsJson = objectMapper.writeValueAsString(request.getImageUrls());
        String pricesJson = objectMapper.writeValueAsString(request.getPrices());

        // Tính lại tổng số lượng tồn kho, giá trị thấp nhất và cao nhất
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
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrices(pricesJson);
        product.setStockQuantity(totalStockQuantity);
        product.setOrigin(request.getOrigin());
        product.setBrand(request.getBrand());
        product.setExpiryDate(request.getExpiryDate());
        product.setCategory(categoryOptional.get());
        product.setImageUrls(imageUrlsJson);
        product.setMinPrice(minPrice);
        product.setMaxPrice(maxPrice);

        productRepository.save(product);

        List<ProductVariant> existingVariants = productVariantRepository.findByProductId(id);
        productVariantRepository.deleteAll(existingVariants);

        List<ProductVariant> savedVariants = new ArrayList<>();
        List<ProductAttribute> savedAttributes = new ArrayList<>();

        for (UpdateProductVariantRequest variantRequest : request.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setPrice(variantRequest.getPrice());
            variant.setStockQuantity(variantRequest.getStockQuantity());
            variant.setImageUrl(variantRequest.getImageUrl());
            variant.setStatus(variantRequest.getStatus());
            variant.setProduct(product);
            savedVariants.add(variant);

            if (variantRequest.getAttributes() != null) {
                for (UpdateProductAttributeRequest attributeRequest : variantRequest.getAttributes()) {
                    ProductAttribute attribute = new ProductAttribute();
                    attribute.setName(attributeRequest.getName());
                    attribute.setValue(attributeRequest.getValue());
                    attribute.setProductVariant(variant);
                    savedAttributes.add(attribute);
                }
            }
        }

        productVariantRepository.saveAll(savedVariants);
        productAttributeRepository.saveAll(savedAttributes);

        // Kiểm tra trạng thái của các biến thể và cập nhật trạng thái sản phẩm
        boolean hasActive = savedVariants.stream().anyMatch(v -> v.getStatus() == ProductStatus.ACTIVE);
        boolean hasOutOfStock = savedVariants.stream().anyMatch(v -> v.getStatus() == ProductStatus.OUT_OF_STOCK);
        boolean allInactive = savedVariants.stream().allMatch(v -> v.getStatus() == ProductStatus.INACTIVE);

        if (hasActive) {
            product.setStatus(ProductStatus.ACTIVE);
        } else if (hasOutOfStock) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (allInactive) {
            product.setStatus(ProductStatus.INACTIVE);
        }
        productRepository.save(product);

        List<CartItem> cartItems = cartItemRepository.findByProductId(product.getId());
        for (CartItem cartItem : cartItems) {
            cartItem.setIsUpdated(true);
        }
        cartItemRepository.saveAll(cartItems);

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
                .status(product.getStatus())
                .build();
    }

    public CommonSearchResponse<?> searchProductByShop(Long id, ProductSearchRequest request) {
        List<SearchProductDto> result = productCustomRepository.searchProductByShop(id, request);
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

    public ProductDetailResponse getProductById(Long id) {
        SearchProductDto result = productCustomRepository.getProductById(id);
        return objectMapper.convertValue(result, ProductDetailResponse.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long productId, ProductStatus status) throws Exception {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new Exception("Product not found");
        }

        Product product = productOptional.get();

        product.setStatus(status);
        productRepository.save(product);

        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);

        for (ProductVariant variant : variants) {
            variant.setStatus(status);
        }
        productVariantRepository.saveAll(variants);

        // Cập nhật trường isDeleted của các CartItem nếu trạng thái là INACTIVE
        if (status == ProductStatus.INACTIVE) {
            List<CartItem> cartItems = cartItemRepository.findByProductId(productId); // Gọi phương thức mới
            for (CartItem cartItem : cartItems) {
                cartItem.setIsInactive(true);
            }
            cartItemRepository.saveAll(cartItems);
        } else {
            List<CartItem> cartItems = cartItemRepository.findByProductId(productId); // Gọi phương thức mới
            for (CartItem cartItem : cartItems) {
                cartItem.setIsInactive(false);
            }
            cartItemRepository.saveAll(cartItems);
        }
    }


}
