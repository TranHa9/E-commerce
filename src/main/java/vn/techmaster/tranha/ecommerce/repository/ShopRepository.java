package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.tranha.ecommerce.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
