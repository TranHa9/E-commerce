package vn.techmaster.tranha.ecommerce.repository.custom;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.SearchShopDto;
import vn.techmaster.tranha.ecommerce.dto.SearchUserDto;
import vn.techmaster.tranha.ecommerce.model.request.ShopSearchRequest;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ShopCustomRepository extends BaseRepository {

    public List<SearchShopDto> searchShop(ShopSearchRequest request) {
        String query = "with raw_data as(\n" +
                "\tselect s.id, s.name, s.logo, s.rating, s.description, s.user_id, u.name as user_name, u.email\n" +
                "    from shops s\n" +
                "    join users u on s.user_id = u.id\n" +
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
            searchCondition += " and lower(u.name) like :name";
            parameters.put("name", "%" + request.getName().toLowerCase() + "%");
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            searchCondition += " and lower(u.email) like :email";
            parameters.put("email", "%" + request.getEmail().toLowerCase() + "%");
        }
        if (request.getUserName() != null && !request.getUserName().trim().isEmpty()) {
            searchCondition += " and lower(u.user_name) like :userName";
            parameters.put("userName", "%" + request.getUserName().toLowerCase() + "%");
        }
        if (request.getRattingStart() != null) {
            searchCondition += " and s.rating >= :rating_start";
            parameters.put("rating_start", request.getRattingStart());
        }
        if (request.getRattingEnd() != null) {
            searchCondition += " and s.rating <= :rating_end";
            parameters.put("rating_end", request.getRattingEnd());
        }

        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchShopDto.class));
    }
}
