package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techmaster.tranha.ecommerce.dto.CartItemDto;
import vn.techmaster.tranha.ecommerce.entity.*;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;
import vn.techmaster.tranha.ecommerce.model.response.CartItemResponse;
import vn.techmaster.tranha.ecommerce.repository.CartItemRepository;
import vn.techmaster.tranha.ecommerce.repository.CartRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.CartItemCustomRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {

    UserRepository userRepository;
    CartRepository cartRepository;
    CartItemCustomRepository cartItemCustomRepository;
    CartItemRepository cartItemRepository;
    ObjectMapper objectMapper;

    public List<CartItemDto> getCartItemByUserId(Long userId) throws Exception {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            return null;
        }
        List<CartItemDto> cartItems = cartItemCustomRepository.getItemsByUser(userId);
        if (cartItems.isEmpty()) {
            return List.of();
        }
        return cartItems;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(Long cartItemId) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isEmpty()) {
            return;
        }
        CartItem cartItem = cartItemOptional.get();
        Optional<Cart> cartOptional = cartRepository.findByUserId(cartItem.getCart().getUser().getId());
        cartItemRepository.deleteById(cartItemId);
        if (cartOptional.isPresent()) {
            updateCart(cartOptional.get());
        }
    }

    private void updateCart(Cart cart) {
        if (cart == null) {
            return;
        }
        // Tính tổng số lượng và tổng giá
        int totalQuantity = cartItemRepository.findTotalQuantityByCartId(cart.getId());
        double totalPrice = cartItemRepository.findTotalPriceByCartId(cart.getId());
        cart.setQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }


}
