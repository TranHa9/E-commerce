package vn.techmaster.tranha.ecommerce.repository.custom;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import vn.techmaster.tranha.ecommerce.dto.SearchVoucherDto;
import vn.techmaster.tranha.ecommerce.model.request.SearchVoucherRequest;
import vn.techmaster.tranha.ecommerce.repository.BaseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VoucherCustomRepository extends BaseRepository {

    public List<SearchVoucherDto> searchVoucherByShop(Long shopId, SearchVoucherRequest request) {
        String query = "with raw_data as (\n" +
                "         select v.id, v.code, v.discount_type, v.start_date, v.end_date,\n" +
                "         v.min_order_value, v.usage_limit, v.voucher_value, v.shop_id, s.name as shopName\n" +
                "    from vouchers v\n" +
                "    join shops s on s.id = v.shop_id\n" +
                "    where 1 = 1 \n" +
                "       and v.shop_id = :shopId" +
                "    {{search_condition}}\n" +
                "), count_data as (\n" +
                "    select count(*) as totalRecord\n" +
                "    from raw_data\n" +
                ")\n" +
                "select r.*, c.totalRecord\n" +
                "from raw_data r, count_data c\n" +
                "limit :p_page_size\n" +
                "offset :p_offset";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("shopId", shopId);
        String searchCondition = "";
        if (request.getSearchCode() != null && !request.getSearchCode().trim().isEmpty()) {
            searchCondition += " and lower(v.code) like :code";
            parameters.put("code", "%" + request.getSearchCode().toLowerCase() + "%");
        }
        if (request.getSearchDiscountType() != null && !request.getSearchDiscountType().trim().isEmpty()) {
            searchCondition += " and v.discount_type = :discountType";
            parameters.put("discountType", request.getSearchDiscountType());
        }

        if (request.getSearchVoucherValue() != null && !request.getSearchVoucherValue().trim().isEmpty()) {
            searchCondition += " and lower(v.voucher_value) like :voucherValue";
            parameters.put("voucherValue", "%" + request.getSearchVoucherValue() + "%");
        }

        query = query.replace("{{search_condition}}", searchCondition);
        parameters.put("p_page_size", request.getPageSize());
        parameters.put("p_offset", request.getPageSize() * request.getPageIndex());

        return getNamedParameterJdbcTemplate().query(query, parameters, new BeanPropertyRowMapper<>(SearchVoucherDto.class));
    }
}
