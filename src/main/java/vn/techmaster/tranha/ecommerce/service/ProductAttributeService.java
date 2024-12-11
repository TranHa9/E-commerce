package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.entity.ProductAttribute;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductAttrbutesRequest;
import vn.techmaster.tranha.ecommerce.model.response.ProductAttributeResponse;
import vn.techmaster.tranha.ecommerce.repository.ProductAttributeRepository;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductAttributeService {

    ProductAttributeRepository productAttributeRepository;
    ObjectMapper objectMapper;

    public ProductAttributeResponse createProductAttribute(CreateProductAttrbutesRequest request) throws Exception {
        if (productAttributeRepository.existsByName(request.getName())) {
            throw new Exception("Product Attribute with this name already exists");
        }
        ProductAttribute productAttribute = ProductAttribute.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        productAttributeRepository.save(productAttribute);
        return objectMapper.convertValue(productAttribute, ProductAttributeResponse.class);
    }
}
