package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.dto.SearchCategroryDto;
import vn.techmaster.tranha.ecommerce.dto.SearchVoucherDto;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.entity.Voucher;
import vn.techmaster.tranha.ecommerce.model.request.CreateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.request.SearchVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.response.CategorySearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.VoucherResponse;
import vn.techmaster.tranha.ecommerce.model.response.VoucherSearchResponse;
import vn.techmaster.tranha.ecommerce.repository.ShopRepository;
import vn.techmaster.tranha.ecommerce.repository.VoucherRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.VoucherCustomRepository;
import vn.techmaster.tranha.ecommerce.statics.AppliedType;
import vn.techmaster.tranha.ecommerce.statics.DiscountType;
import vn.techmaster.tranha.ecommerce.statics.OwnerType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    ShopRepository shopRepository;
    VoucherCustomRepository voucherCustomRepository;
    ObjectMapper objectMapper;

    public VoucherResponse createVoucher(CreateVoucherRequest request) throws Exception {
        Optional<Shop> shopOptional = shopRepository.findById(request.getShopId());
        if (shopOptional.isEmpty()) {
            throw new Exception("shop not found");
        }
        Shop shop = shopOptional.get();
        Voucher voucher = Voucher.builder()
                .shop(shop)
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .voucherValue(request.getVoucherValue())
                .minOrderValue(request.getMinOrderValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .build();
        voucherRepository.save(voucher);
        return objectMapper.convertValue(voucher, VoucherResponse.class);
    }

    public CommonSearchResponse<?> getVouchersByShop(Long shopId, SearchVoucherRequest request) throws Exception {
        List<SearchVoucherDto> result = voucherCustomRepository.searchVoucherByShop(shopId, request);
        Long totalRecord = 0L;
        List<VoucherSearchResponse> voucherResponses = new ArrayList<>();
        if (!result.isEmpty()) {
            totalRecord = result.get(0).getTotalRecord();
            voucherResponses = result
                    .stream()
                    .map(s -> objectMapper.convertValue(s, VoucherSearchResponse.class))
                    .toList();
        }

        int totalPage = (int) Math.ceil((double) totalRecord / request.getPageSize());

        return CommonSearchResponse.<VoucherSearchResponse>builder()
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .data(voucherResponses)
                .pageInfo(new CommonSearchResponse.CommonPagingResponse(request.getPageSize(), request.getPageIndex()))
                .build();
    }

    public VoucherResponse updateVoucher(Long id, UpdateVoucherRequest request) throws Exception {
        Optional<Shop> shopOptional = shopRepository.findById(request.getShopId());
        if (shopOptional.isEmpty()) {
            throw new Exception("shop not found");
        }
        Shop shop = shopOptional.get();
        Optional<Voucher> existingVoucher = voucherRepository.findById(id);
        if (existingVoucher.isEmpty()) {
            throw new Exception("voucher not found");
        }
        Voucher voucher = existingVoucher.get();
        voucher.setCode(request.getCode());
        voucher.setShop(shop);
        voucher.setDiscountType(request.getDiscountType());
        voucher.setVoucherValue(request.getVoucherValue());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setUsageLimit(request.getUsageLimit());
        voucherRepository.save(voucher);
        return objectMapper.convertValue(voucher, VoucherResponse.class);
    }

    public VoucherResponse getVoucherDetail(Long id) throws Exception {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new Exception("Voucher not found"));
        return objectMapper.convertValue(voucher, VoucherResponse.class);
    }
}
