package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.model.request.CreateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.request.SearchVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.request.ValidateVoucherRequest;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.model.response.ValidateVoucherResponse;
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
    public VoucherResponse createVoucher(@RequestBody @Valid CreateVoucherRequest request) throws Exception {
        return voucherService.createVoucher(request);
    }

    @GetMapping("/shop/{shopId}")
    public CommonSearchResponse<?> getVouchersByShop(@PathVariable Long shopId, SearchVoucherRequest request) throws Exception {
        return voucherService.getVouchersByShop(shopId, request);

    }

    @GetMapping("/{id}")
    public VoucherResponse getVoucherDetail(@PathVariable Long id) throws Exception {
        return voucherService.getVoucherDetail(id);

    }

    @PutMapping("/{id}")
    public VoucherResponse updateVoucher(@PathVariable Long id, @RequestBody UpdateVoucherRequest request) throws Exception {
        return voucherService.updateVoucher(id, request);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateVoucher(@RequestBody ValidateVoucherRequest request) {
        ValidateVoucherResponse response = voucherService.validateVoucher(request);
        return ResponseEntity.ok(response);
    }
}
