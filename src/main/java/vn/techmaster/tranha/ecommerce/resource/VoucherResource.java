package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.entity.Voucher;
import vn.techmaster.tranha.ecommerce.model.request.CreateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.response.VoucherResponse;
import vn.techmaster.tranha.ecommerce.service.VoucherService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/vouchers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherResource {

    VoucherService voucherService;

    @PostMapping
    public VoucherResponse createVoucher(@RequestBody @Valid CreateVoucherRequest request) {
        return voucherService.createVoucher(request);
    }

    @GetMapping("/shop/{shopId}")
    public List<VoucherResponse> getVouchersByShop(@PathVariable Long shopId) {
        return voucherService.getVouchersByShop(shopId);
    
    }
}
