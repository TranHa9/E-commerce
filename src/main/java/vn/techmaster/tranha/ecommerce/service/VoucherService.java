package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.entity.Shop;
import vn.techmaster.tranha.ecommerce.entity.Voucher;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CreateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.response.VoucherResponse;
import vn.techmaster.tranha.ecommerce.repository.ShopRepository;
import vn.techmaster.tranha.ecommerce.repository.VoucherRepository;
import vn.techmaster.tranha.ecommerce.statics.AppliedType;
import vn.techmaster.tranha.ecommerce.statics.DiscountType;
import vn.techmaster.tranha.ecommerce.statics.OwnerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    ShopRepository shopRepository;
    ObjectMapper objectMapper;

    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        Optional<Shop> shopOptional = shopRepository.findById(request.getShopId());
        if (shopOptional.isEmpty()) {
            new ObjectNotFoundException("shop not found");
            return new VoucherResponse();
        }
        Shop shop = shopOptional.get();
        Voucher voucher = Voucher.builder()
                .shop(shop)
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .voucherValue(request.getVoucherValue())
                .minOrderValue(request.getMinOrderValue())
                .appliedType(request.getAppliedType())
                .ownerType(request.getOwnerType())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .build();
        voucherRepository.save(voucher);
        return objectMapper.convertValue(voucher, VoucherResponse.class);
    }

    public List<VoucherResponse> getVouchersByShop(Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByShopId(shopId);
        return vouchers.stream().map(voucher -> objectMapper.convertValue(voucher, VoucherResponse.class)).collect(Collectors.toList());
    }
}
