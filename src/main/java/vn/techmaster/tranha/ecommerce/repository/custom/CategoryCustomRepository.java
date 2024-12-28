package vn.techmaster.tranha.ecommerce.repository.custom;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.SearchCategroryDto;
import vn.techmaster.tranha.ecommerce.dto.SearchShopDto;
import vn.techmaster.tranha.ecommerce.model.request.CategorySearchRequest;
import vn.techmaster.tranha.ecommerce.model.response.CategorySearchResponse;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CategoryCustomRepository extends BaseRepository {
    public List<SearchCategroryDto> searchCategory(CategorySearchRequest request) {
        String query = "with raw_data as(\n" +
                "\tselect c.id, c.name, c.description, c.status\n" +
                "    from categories c\n" +
                "    where 1 = 1\n" +
                "    {{search_condition}}\n" +
                "), count_data as(\n" +
                "\tselect count(*) totalRecord\n" +
                "    from raw_data\n" +
                ")\n" +
                "select r.*, c.totalRecord\n" +
                "from raw_data r, count_data c\n" +
                "limit :p_page_size\n" +
                "offset :p_offset";

        Map<String, Object> parameters = new HashMap<>();
        String searchCondition = "";
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            searchCondition += " and lower(c.name) like :name";
            parameters.put("name", "%" + request.getName().toLowerCase() + "%");
        }
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            searchCondition += " and c.status = :status";
            parameters.put("status", request.getStatus());
        }

        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchCategroryDto.class));
    }
}
