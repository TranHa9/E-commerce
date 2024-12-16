package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.tranha.ecommerce.entity.Shop;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByUserId(Long userId);
}
