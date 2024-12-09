package vn.techmaster.tranha.ecommerce.repository.custom;

import vn.techmaster.tranha.ecommerce.dto.SearchUserDto;
import vn.techmaster.tranha.ecommerce.model.request.UserSearchRequest;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserCustomRepository extends BaseRepository {

    public List<SearchUserDto> searchUser(UserSearchRequest request) {
        String query = "with raw_data as (\n" +
                "    select u.id, u.name, u.email, u.phone, u.dob, u.gender, u.status, u.avatar, r.name as role\n" +
                "    from users u\n" +
                "    join user_role ur on u.id = ur.user_id\n" +
                "    join roles r on ur.role_id = r.id\n" +
                "    where 1 = 1\n" +
                "   {{search_condition}}\n" +
                "), count_data as(\n" +
                "    select count(*) totalRecord\n" +
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
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            searchCondition += " and u.phone like :phone";
            parameters.put("phone", "%" + request.getPhone() + "%");
        }

        if (request.getRole() != null) {
            searchCondition += " and lower(r.name) = :role";
            parameters.put("role", request.getRole().toString());
        }

        if (request.getStatus() != null) {
            searchCondition += " and u.status = :status";
            parameters.put("status", request.getStatus().toString());
        }
        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchUserDto.class));
    }

}
