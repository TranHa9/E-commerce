package vn.techmaster.tranha.ecommerce.repository.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.ProductVariantDto;
import vn.techmaster.tranha.ecommerce.dto.SearchProductAllDto;
import vn.techmaster.tranha.ecommerce.model.request.CreateProductRequest;
import vn.techmaster.tranha.ecommerce.model.request.ProductSearchRequest;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCustomRepository extends BaseRepository {

    ObjectMapper objectMapper;

    public List<SearchProductAllDto> searchProductAll(ProductSearchRequest request) {
        String query = "with raw_data as (\n" +
                "         select p.id, p.name as product_name, p.prices as product_prices, p.average_rating, p.description, p.image_urls as product_images, p.max_price, p.min_price, \n" +
                "                p.origin,p.brand, p.status, p.expiry_date, p.stock_quantity as product_stock_quantity, p.sold_quantity, categories.name as category_name, s.name as shop_name," +
                "    JSON_ARRAYAGG(\n" +
                "            JSON_OBJECT(\n" +
                "                'id', pv.id,\n" +
                "                'price', pv.price, \n" +
                "                'stock_quantity', pv.stock_quantity, \n" +
                "                'image', pv.image_url, \n" +
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
                "    group by p.id, p.name, p.prices, p.average_rating, p.description, p.image_urls, p.max_price, p.min_price, p.origin,p.brand, p.status, \n" +
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
        if (request.getProductName() != null && !request.getProductName().trim().isEmpty()) {
            searchCondition += " and lower(p.name) like :name";
            parameters.put("name", "%" + request.getProductName().toLowerCase() + "%");
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
        if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {
            searchCondition += " and lower(categories.name) like :categoryName";
            parameters.put("categoryName", "%" + request.getCategoryName().toLowerCase() + "%");
        }
        if (request.getShopName() != null && !request.getShopName().trim().isEmpty()) {
            searchCondition += " and lower(s.name) like :shopName";
            parameters.put("shopName", "%" + request.getShopName().toLowerCase() + "%");
        }
        if (request.getBrand() != null && !request.getBrand().trim().isEmpty()) {
            searchCondition += " and lower(p.brand) like :brand";
            parameters.put("brand", "%" + request.getBrand().toLowerCase() + "%");
        }
        if (request.getStatus() != null) {
            searchCondition += " and lower(p.status) = :status";
            parameters.put("status", request.getStatus().name().toLowerCase());
        }

        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

//        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchProductDto.class));
        List<SearchProductAllDto> result = getNamedParameterJdbcTemplate().query(query, parameters, (rs, rowNum) -> {
            SearchProductAllDto dto = new SearchProductAllDto();
            dto.setId(rs.getLong("id"));
            dto.setProductName(rs.getString("product_name"));
            dto.setAverageRating(rs.getDouble("average_rating"));
            dto.setDescription(rs.getString("description"));
            dto.setMaxPrice(rs.getDouble("max_price"));
            dto.setMinPrice(rs.getDouble("min_price"));
            dto.setOrigin(rs.getString("origin"));
            dto.setBrand(rs.getString("brand"));
            dto.setExpiryDate(rs.getString("expiry_date"));
            dto.setProductStockQuantity(rs.getInt("product_stock_quantity"));
            dto.setSoldQuantity(rs.getInt("sold_quantity"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setShopName(rs.getString("shop_name"));
            dto.setStatus(rs.getString("status"));
            dto.setTotalRecord(rs.getLong("totalRecord"));

            String pricesJson = rs.getString("product_prices");
            if (pricesJson != null && !pricesJson.isEmpty()) {
                try {
                    List<CreateProductRequest.Prices> prices = objectMapper.readValue(pricesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CreateProductRequest.Prices.class));
                    dto.setPrices(prices);
                } catch (Exception e) {
                    dto.setVariants(new ArrayList<>());
                }
            }
            String imagesJson = rs.getString("product_images");
            if (imagesJson != null && !imagesJson.isEmpty()) {
                try {
                    List<String> images = objectMapper.readValue(imagesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    dto.setProductImages(images);
                } catch (Exception e) {
                    dto.setVariants(new ArrayList<>());
                }
            }

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

    public List<SearchProductAllDto> searchProductByShop(Long id, ProductSearchRequest request) {
        String query = "with raw_data as (\n" +
                "         select p.id, p.name as product_name, p.prices as product_prices, p.average_rating, p.description, p.image_urls as product_images, p.max_price, p.min_price, \n" +
                "                p.origin,p.brand, p.status, p.expiry_date, p.stock_quantity as product_stock_quantity, p.sold_quantity, categories.name as category_name, s.name as shop_name," +
                "    JSON_ARRAYAGG(\n" +
                "            JSON_OBJECT(\n" +
                "                'id', pv.id,\n" +
                "                'price', pv.price, \n" +
                "                'stock_quantity', pv.stock_quantity, \n" +
                "                'image', pv.image_url, \n" +
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
                "        and p.shop_id = :shopId\n" +
                "   {{search_condition}}\n" +
                "    group by p.id, p.name, p.prices, p.average_rating, p.description, p.image_urls, p.max_price, p.min_price, p.origin,  \n" +
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
        parameters.put("shopId", id);
        String searchCondition = "";
        if (request.getProductName() != null && !request.getProductName().trim().isEmpty()) {
            searchCondition += " and lower(p.name) like :name";
            parameters.put("name", "%" + request.getProductName().toLowerCase() + "%");
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
        if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {
            searchCondition += " and lower(categories.name) like :categoryName";
            parameters.put("categoryName", "%" + request.getCategoryName().toLowerCase() + "%");
        }
        if (request.getBrand() != null && !request.getBrand().trim().isEmpty()) {
            searchCondition += " and lower(p.brand) like :brand";
            parameters.put("brand", "%" + request.getBrand().toLowerCase() + "%");
        }
        if (request.getStatus() != null) {
            searchCondition += " and p.status = :status";
            parameters.put("status", request.getStatus().name());
        }

        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

//        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchProductDto.class));
        List<SearchProductAllDto> result = getNamedParameterJdbcTemplate().query(query, parameters, (rs, rowNum) -> {
            SearchProductAllDto dto = new SearchProductAllDto();
            dto.setId(rs.getLong("id"));
            dto.setProductName(rs.getString("product_name"));
            dto.setAverageRating(rs.getDouble("average_rating"));
            dto.setDescription(rs.getString("description"));
            dto.setMaxPrice(rs.getDouble("max_price"));
            dto.setMinPrice(rs.getDouble("min_price"));
            dto.setOrigin(rs.getString("origin"));
            dto.setBrand(rs.getString("brand"));
            dto.setExpiryDate(rs.getString("expiry_date"));
            dto.setProductStockQuantity(rs.getInt("product_stock_quantity"));
            dto.setSoldQuantity(rs.getInt("sold_quantity"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setShopName(rs.getString("shop_name"));
            dto.setStatus(rs.getString("status"));
            dto.setTotalRecord(rs.getLong("totalRecord"));

            String pricesJson = rs.getString("product_prices");
            if (pricesJson != null && !pricesJson.isEmpty()) {
                try {
                    List<CreateProductRequest.Prices> prices = objectMapper.readValue(pricesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CreateProductRequest.Prices.class));
                    dto.setPrices(prices);
                } catch (Exception e) {
                    dto.setVariants(new ArrayList<>());
                }
            }
            String imagesJson = rs.getString("product_images");
            if (imagesJson != null && !imagesJson.isEmpty()) {
                try {
                    List<String> images = objectMapper.readValue(imagesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    dto.setProductImages(images);
                } catch (Exception e) {
                    dto.setVariants(new ArrayList<>());
                }
            }

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

    public SearchProductAllDto getProductById(Long id) {
        String query = "select p.id, p.name as product_name, p.prices as product_prices, p.average_rating, p.description, p.image_urls as product_images, p.max_price, p.min_price, \n" +
                "                p.origin,p.brand,p.status, p.expiry_date, p.stock_quantity as product_stock_quantity, p.sold_quantity, categories.id as categoryId, categories.name as category_name, s.name as shop_name," +
                "    JSON_ARRAYAGG(\n" +
                "            JSON_OBJECT(\n" +
                "                'id', pv.id,\n" +
                "                'price', pv.price, \n" +
                "                'stock_quantity', pv.stock_quantity, \n" +
                "                'image', pv.image_url, \n" +
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
                "        and p.id = :productId\n" +
                "    group by p.id, p.name, p.prices, p.average_rating, p.description, p.image_urls, p.max_price, p.min_price, p.origin,  \n" +
                "             p.expiry_date, p.stock_quantity, p.sold_quantity, categories.name, s.name\n";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("productId", id);

//        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchProductDto.class));
        return getNamedParameterJdbcTemplate().queryForObject(query, parameters, (rs, rowNum) -> {
            SearchProductAllDto dto = new SearchProductAllDto();
            dto.setId(rs.getLong("id"));
            dto.setProductName(rs.getString("product_name"));
            dto.setAverageRating(rs.getDouble("average_rating"));
            dto.setDescription(rs.getString("description"));
            dto.setMaxPrice(rs.getDouble("max_price"));
            dto.setMinPrice(rs.getDouble("min_price"));
            dto.setOrigin(rs.getString("origin"));
            dto.setBrand(rs.getString("brand"));
            dto.setExpiryDate(rs.getString("expiry_date"));
            dto.setProductStockQuantity(rs.getInt("product_stock_quantity"));
            dto.setSoldQuantity(rs.getInt("sold_quantity"));
            dto.setCategoryId(rs.getLong("categoryId"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setShopName(rs.getString("shop_name"));
            dto.setStatus(rs.getString("status"));

            String pricesJson = rs.getString("product_prices");
            if (pricesJson != null && !pricesJson.isEmpty()) {
                try {
                    List<CreateProductRequest.Prices> prices = objectMapper.readValue(pricesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CreateProductRequest.Prices.class));
                    dto.setPrices(prices);
                } catch (Exception e) {
                    dto.setPrices(new ArrayList<>());
                }
            }

            String imagesJson = rs.getString("product_images");
            if (imagesJson != null && !imagesJson.isEmpty()) {
                try {
                    List<String> images = objectMapper.readValue(imagesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    dto.setProductImages(images);
                } catch (Exception e) {
                    dto.setProductImages(new ArrayList<>());
                }
            }

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
    }
}
