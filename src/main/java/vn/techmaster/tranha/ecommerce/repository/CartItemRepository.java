package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.techmaster.tranha.ecommerce.entity.Cart;
import vn.techmaster.tranha.ecommerce.entity.CartItem;
import vn.techmaster.tranha.ecommerce.entity.Product;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByProductId(Long id);

    @Query("SELECT c FROM CartItem c WHERE c.cart = :cart AND c.product = :product AND c.variants = :variants")
    Optional<CartItem> findByCartAndProductAndVariants(Cart cart, Product product, String variants);

    @Query("SELECT COALESCE(SUM(c.quantity),0) FROM CartItem c WHERE c.cart.id = :cartId")
    Integer findTotalQuantityByCartId(Long cartId);

    @Query("SELECT COALESCE(SUM(c.totalPrice),0) FROM CartItem c WHERE c.cart.id = :cartId")
    Double findTotalPriceByCartId(Long cartId);
}
