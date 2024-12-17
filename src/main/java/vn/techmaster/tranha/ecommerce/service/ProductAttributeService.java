package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.entity.ProductAttribute;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductAttributesRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductAttributeResponse;
import vn.techmaster.tranha.ecommerce.repository.ProductAttributeRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductVariantRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductAttributeService {

    ProductAttributeRepository productAttributeRepository;
    ProductVariantRepository productVariantRepository;
    ObjectMapper objectMapper;

    public ProductAttributeResponse createProductAttribute(CreateProductAttributesRequest request) throws Exception {
//        if (productAttributeRepository.existsByName(request.getName())) {
//            throw new Exception("Product Attribute with this name already exists");
//        }
        Optional<ProductVariant> productVariantOptional = productVariantRepository.findById(request.getVariantId());
        if (productVariantOptional.isEmpty()) {
            throw new Exception("Product Variant not found");
        }

        ProductVariant productVariant = productVariantOptional.get();

        ProductAttribute productAttribute = ProductAttribute.builder()
                .name(request.getName())
                .productVariant(productVariant)
                .value(request.getValue())
                .build();
        productAttributeRepository.save(productAttribute);
        return objectMapper.convertValue(productAttribute, ProductAttributeResponse.class);
    }
}
