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

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.cart.id = :cartId")
    Integer findTotalQuantityByCartId(Long cartId);

    @Query("SELECT SUM(c.totalPrice) FROM CartItem c WHERE c.cart.id = :cartId")
    Double findTotalPriceByCartId(Long cartId);
}
