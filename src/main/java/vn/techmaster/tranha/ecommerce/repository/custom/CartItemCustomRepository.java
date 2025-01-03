package vn.techmaster.tranha.ecommerce.repository.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.CartItemDto;
import vn.techmaster.tranha.ecommerce.model.response.CartItemResponse;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemCustomRepository extends BaseRepository {

    ObjectMapper objectMapper;

    public List<CartItemDto> getItemsByUser(Long userId) {
        String query = "SELECT \n" +
                "    ci.id,\n" +
                "    ci.cart_id,\n" +
                "    ci.product_id,\n" +
                "    ci.quantity,\n" +
                "    ci.unit_price,\n" +
                "    ci.total_price,\n" +
                "    ci.is_updated,\n" +
                "    ci.is_inactive,\n" +
                "    p.name,\n" +
                "    p.image_urls,\n" +
                "    ci.variants,\n" +
                "    (\n" +
                "        SELECT pv.stock_quantity\n" +
                "        FROM product_variants pv\n" +
                "        JOIN product_attributes pa ON pa.product_variant_id = pv.id\n" +
                "        WHERE pv.product_id = ci.product_id \n" +
                "        AND JSON_CONTAINS(ci.variants, JSON_OBJECT('name', pa.name, 'value', pa.value))\n" +
                "        LIMIT 1\n" +
                "    ) AS maxQuantity\n" +
                "FROM cart_items ci\n" +
                "JOIN products p on p.id = ci.product_id\n" +
                "JOIN carts c on c.id = ci.cart_id\n" +
                "where c.user_id = :userId\n" +
                "ORDER BY ci.created_at DESC;";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        try {
            return getNamedParameterJdbcTemplate().query(query, parameters, (rs, rowNum) -> {
                CartItemDto dto = new CartItemDto();
                dto.setId(rs.getLong("id"));
                dto.setCartId(rs.getLong("cart_id"));
                dto.setProductId(rs.getLong("product_id"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setUnitPrice(rs.getDouble("unit_price"));
                dto.setTotalPrice(rs.getDouble("total_price"));
                dto.setIsUpdated(rs.getBoolean("is_updated"));
                dto.setIsInactive(rs.getBoolean("is_inactive"));
                dto.setMaxQuantity(rs.getInt("maxQuantity"));
                dto.setProductName(rs.getString("name"));

                String imageJson = rs.getString("image_urls");
                if (imageJson != null) {
                    try {
                        List<String> imageUrls = objectMapper.readValue(imageJson, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                        dto.setImageUrls(imageUrls);
                    } catch (JsonProcessingException e) {
                        dto.setVariants(new ArrayList<>());
                        throw new RuntimeException(e);
                    }
                }

                // Chuyển đổi chuỗi JSON thành List<CartItemDto.Attributes>
                String variantsJson = rs.getString("variants");
                if (variantsJson != null) {
                    try {
                        List<CartItemDto.Attributes> variants = objectMapper.readValue(variantsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemDto.Attributes.class));
                        dto.setVariants(variants);
                    } catch (JsonProcessingException e) {
                        dto.setVariants(new ArrayList<>());
                        throw new RuntimeException(e);
                    }
                }
                return dto;
            });
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
