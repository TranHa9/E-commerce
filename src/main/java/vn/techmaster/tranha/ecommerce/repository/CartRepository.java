package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.tranha.ecommerce.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
