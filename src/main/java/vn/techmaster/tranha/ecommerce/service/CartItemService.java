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
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;
import vn.techmaster.tranha.ecommerce.model.response.CartItemResponse;
import vn.techmaster.tranha.ecommerce.repository.CartItemRepository;
import vn.techmaster.tranha.ecommerce.repository.CartRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.CartItemCustomRepository;

import java.util.ArrayList;
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
            return new ArrayList<>();
        }
        List<CartItemDto> cartItems = cartItemCustomRepository.getItemsByUser(userId);
        if (cartItems.isEmpty()) {
            return new ArrayList<>();
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

    @Transactional(rollbackFor = Exception.class)
    public void updateCartItemQuantity(Long cartItemId, int newQuantity) throws ObjectNotFoundException {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isEmpty()) {
            throw new ObjectNotFoundException("Cart item not found");
        }
        CartItem cartItem = cartItemOptional.get();
        // Kiểm tra tồn kho
        ProductVariant productVariant = cartItem.getProduct().getVariants().stream()
                .filter(variant -> {
                    try {
                        List<CreateCartRequest.Attributes> cartItemAttributes = objectMapper.readValue(
                                cartItem.getVariants(), new TypeReference<List<CreateCartRequest.Attributes>>() {
                                });
                        // So sánh từng thuộc tính
                        for (CreateCartRequest.Attributes cartAttr : cartItemAttributes) {
                            boolean match = variant.getAttributes().stream()
                                    .anyMatch(attr -> attr.getName().equals(cartAttr.getName())
                                            && attr.getValue().equals(cartAttr.getValue()));
                            if (!match) {
                                return false;
                            }
                        }
                        return true;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing cart item attributes", e);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Product variant not found for the cart item"));

        if (newQuantity > productVariant.getStockQuantity()) {
            throw new IllegalArgumentException("Not enough stock for the requested quantity");
        }
        // Cập nhật số lượng và tổng giá cho mục giỏ hàng
        cartItem.setQuantity(newQuantity);
        cartItem.setTotalPrice(cartItem.getUnitPrice() * newQuantity);
        cartItemRepository.save(cartItem);
        // Cập nhật giỏ hàng
        updateCart(cartItem.getCart());
    }
}
