package vn.techmaster.tranha.ecommerce.repository.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.CartDto;
import vn.techmaster.tranha.ecommerce.dto.CartItemDto;
import vn.techmaster.tranha.ecommerce.dto.ProductVariantDto;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartCustomRepository extends BaseRepository {

    ObjectMapper objectMapper;

    public CartDto getCartWithItemsByUser(Long userId) {
        String query = "SELECT c.id, c.quantity, c.total_price, c.user_id,\n" +
                "    JSON_ARRAYAGG(\n" +
                "        JSON_OBJECT(\n" +
                "            'id', ci.id,\n" +
                "            'quantity', ci.quantity,\n" +
                "            'unitPrice', ci.unit_price,\n" +
                "            'totalPrice', ci.total_price,\n" +
                "            'variants', JSON_EXTRACT(ci.variants, '$'),\n" +
                "            'product', JSON_OBJECT(\n" +
                "                'id', p.id,\n" +
                "                'name', p.name,\n" +
                "                'prices', JSON_EXTRACT(p.prices, '$'),\n" +
                "                'imageUrls', JSON_EXTRACT(p.image_urls, '$')\n" +
                "            )\n" +
                "        )\n" +
                "    ) AS cartItems\n" +
                "FROM carts c\n" +
                "LEFT JOIN cart_items ci ON ci.cart_id = c.id\n" +
                "LEFT JOIN products p ON ci.product_id = p.id\n" +
                "WHERE c.user_id = :userId\n" +
                "GROUP BY c.id, c.quantity, c.total_price, c.user_id;";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        try {
            return getNamedParameterJdbcTemplate().queryForObject(query, parameters, (rs, rowNum) -> {
                CartDto dto = new CartDto();
                dto.setId(rs.getLong("id"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setTotalPrice(rs.getDouble("total_price"));
                dto.setUserId(rs.getLong("user_id"));

                // Chuyển đổi chuỗi JSON của cartItems thành List<CartItemResponse>
                String cartItemsJson = rs.getString("cartItems");
                if (cartItemsJson != null) {
                    try {
                        List<CartItemDto> cartItems = objectMapper.readValue(cartItemsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemDto.class));
                        dto.setCartItems(cartItems);
                    } catch (JsonProcessingException e) {
                        dto.setCartItems(new ArrayList<>());
                        throw new RuntimeException(e);
                    }
                }
                return dto;
            });
        } catch (EmptyResultDataAccessException e) {
            // Nếu không tìm thấy giỏ hàng, trả về null
            return null;
        }
    }
}
