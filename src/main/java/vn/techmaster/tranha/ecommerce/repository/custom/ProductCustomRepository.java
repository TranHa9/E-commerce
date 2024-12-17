package vn.techmaster.tranha.ecommerce.repository.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.ProductVariantDto;
import vn.techmaster.tranha.ecommerce.dto.SearchProductDto;
import vn.techmaster.tranha.ecommerce.entity.ProductVariant;
import vn.techmaster.tranha.ecommerce.model.request.ProductSearchRequest;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;
import vn.techmaster.tranha.ecommerce.statics.VariantStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCustomRepository extends BaseRepository {

    ObjectMapper objectMapper;

    public List<SearchProductDto> searchProduct(ProductSearchRequest request) {
        String query = "with raw_data as (\n" +
                "         select p.id, p.name as product_name, p.base_price as product_price, p.average_rating, p.description, p.image_urls as product_images, p.max_price, p.min_price, \n" +
                "                p.origin,p.brand, p.expiry_date, p.stock_quantity as product_stock_quantity, p.sold_quantity, categories.name as category_name, s.name as shop_name," +
                "    JSON_ARRAYAGG(\n" +
                "            JSON_OBJECT(\n" +
                "                'id', pv.id,\n" +
                "                'price', pv.price, \n" +
                "                'stock_quantity', pv.stock_quantity, \n" +
                "                'image', pv.image_urls, \n" +
                "                'status', pv.status,\n" +
                "                'attributes', (\n" +
                "                    SELECT JSON_ARRAYAGG(\n" +
                "                        JSON_OBJECT(\n" +
                "                            'name', pa.name, \n" +
                "                            'value', pa.value\n" +
                "                        )\n" +
                "                    )\n" +
                "                    FROM product_attributes pa\n" +
                "                    WHERE pa.product_variant_id = pv.id\n" +
                "                )\n" +
                "            )\n" +
                "        ) AS variants\n" +
                "    from products p\n" +
                "    join categories on categories.id = p.category_id\n" +
                "    left join product_variants pv on pv.product_id = p.id\n" +
                "    join shops s on s.id = p.shop_id\n" +
                "    where 1 = 1 \n" +
                "   {{search_condition}}\n" +
                "    group by p.id, p.name, p.base_price, p.average_rating, p.description, p.image_urls, p.max_price, p.min_price, p.origin,  \n" +
                "             p.expiry_date, p.stock_quantity, p.sold_quantity, categories.name, s.name\n" +
                "), count_data as (\n" +
                "    select count(*) as totalRecord\n" +
                "    from raw_data\n" +
                ")\n" +
                "select r.*, c.totalRecord\n" +
                "from raw_data r, count_data c\n" +
                "limit :p_page_size\n" +
                "offset :p_offset";
        Map<String, Object> parameters = new HashMap<>();
        String searchCondition = "";
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            searchCondition += " and lower(p.name) like :name";
            parameters.put("name", "%" + request.getName().toLowerCase() + "%");
        }
        if (request.getBasePrice() != null && request.getBasePrice() >= 0) {
            searchCondition += " and p.base_price >= :basePrice";
            parameters.put("basePrice", request.getBasePrice());
        }
        if (request.getMinPrice() != null && request.getMinPrice() >= 0) {
            searchCondition += " and p.min_price >= :minPrice";
            parameters.put("minPrice", request.getMinPrice());
        }
        if (request.getMaxPrice() != null && request.getMaxPrice() >= 0) {
            searchCondition += " and p.max_price <= :maxPrice";
            parameters.put("maxPrice", request.getMaxPrice());
        }
        if (request.getStockQuantity() > 0) {
            searchCondition += " and p.stock_quantity >= :stockQuantity";
            parameters.put("stockQuantity", request.getStockQuantity());
        }
        if (request.getAverageRating() != null && request.getAverageRating() > 0) {
            searchCondition += " and p.average_rating >= :averageRating";
            parameters.put("averageRating", request.getAverageRating());
        }
        if (request.getSoldQuantity() > 0) {
            searchCondition += " and p.sold_quantity >= :soldQuantity";
            parameters.put("soldQuantity", request.getSoldQuantity());
        }
        if (request.getCategory() != null && request.getCategory().getName() != null) {
            searchCondition += " and lower(categories.name) like :categoryName";
            parameters.put("categoryName", "%" + request.getCategory().getName().toLowerCase() + "%");
        }
        if (request.getShop() != null && request.getShop().getName() != null) {
            searchCondition += " and lower(s.name) like :shopName";
            parameters.put("shopName", "%" + request.getShop().getName().toLowerCase() + "%");
        }
        if (request.getBrand() != null && !request.getBrand().trim().isEmpty()) {
            searchCondition += " and lower(p.brand) like :brand";
            parameters.put("brand", "%" + request.getBrand().toLowerCase() + "%");
        }

        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

//        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchProductDto.class));
        List<SearchProductDto> result = getNamedParameterJdbcTemplate().query(query, parameters, (rs, rowNum) -> {
            SearchProductDto dto = new SearchProductDto();
            dto.setId(rs.getLong("id"));
            dto.setProductName(rs.getString("product_name"));
            dto.setAverageRating(rs.getDouble("average_rating"));
            dto.setDescription(rs.getString("description"));
            dto.setProductImages(rs.getString("product_images"));
            dto.setMaxPrice(rs.getDouble("max_price"));
            dto.setMinPrice(rs.getDouble("min_price"));
            dto.setBasePrice(rs.getDouble("product_price"));
            dto.setOrigin(rs.getString("origin"));
            dto.setBrand(rs.getString("brand"));
            dto.setExpiryDate(rs.getString("expiry_date"));
            dto.setProductStockQuantity(rs.getInt("product_stock_quantity"));
            dto.setSoldQuantity(rs.getInt("sold_quantity"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setShopName(rs.getString("shop_name"));
            dto.setTotalRecord(rs.getLong("totalRecord"));

            String variantsJson = rs.getString("variants");
            if (variantsJson != null && !variantsJson.isEmpty()) {
                try {
                    List<ProductVariantDto> variants = objectMapper.readValue(variantsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, ProductVariantDto.class));
                    dto.setVariants(variants);
                } catch (Exception e) {
                    dto.setVariants(new ArrayList<>());
                }
            }
            return dto;
        });

        return result;
    }

}
