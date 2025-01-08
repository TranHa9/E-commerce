package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.tranha.ecommerce.entity.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    // Tìm voucher theo mã
    Optional<Voucher> findByCode(String code);

    // Lấy danh sách voucher của shop
    List<Voucher> findByShopId(Long shopId);
}
