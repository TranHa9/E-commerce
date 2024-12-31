package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techmaster.tranha.ecommerce.entity.*;
import vn.techmaster.tranha.ecommerce.model.request.CreateCartRequest;
import vn.techmaster.tranha.ecommerce.model.response.CartResponse;
import vn.techmaster.tranha.ecommerce.repository.CartItemRepository;
import vn.techmaster.tranha.ecommerce.repository.CartRepository;
import vn.techmaster.tranha.ecommerce.repository.ProductRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.statics.CategoryStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    CartItemRepository cartItemRepository;
    ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public CartResponse createCart(Long id, CreateCartRequest request) throws Exception {
        Optional<Product> productOptional = productRepository.findById(request.getProductId());
        if (productOptional.isEmpty()) {
            throw new Exception("Product not found");
        }
        Product product = productOptional.get();

        // Tìm biến thể sản phẩm tương ứng với các thuộc tính người dùng nhập vào
        Optional<ProductVariant> productVariantOptional = findProductVariantByAttributes(product, request.getAttributes());

        if (productVariantOptional.isEmpty()) {
            throw new Exception("Product variant not found with the given attributes");
        }
        ProductVariant productVariant = productVariantOptional.get();

        // Kiểm tra kho có đủ số lượng cho biến thể này
        if (productVariant.getStockQuantity() < request.getQuantity()) {
            throw new Exception("Not enough stock for this product variant");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new Exception("User not found");
        }
        User user = userOptional.get();

        Cart cart = cartRepository.findByUserId(id)
                .orElseGet(() -> {
                    // Nếu không có giỏ hàng, tạo giỏ hàng mới
                    Cart newCart = Cart.builder()
                            .user(user)
                            .quantity(request.getQuantity())
                            .totalPrice(request.getTotalPrice())
                            .build();
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        String attributeJson = objectMapper.writeValueAsString(request.getAttributes());
        System.out.println(attributeJson);
        if (existingCartItem.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng và tổng giá trị
            CartItem cartItem = existingCartItem.get();
            cartItem.setVariants(attributeJson);
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setTotalPrice(cartItem.getUnitPrice() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo một CartItem mới
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .variants(attributeJson)
                    .unitPrice(request.getUnitPrice())
                    .totalPrice(request.getUnitPrice() * request.getQuantity())
                    .build();
            cartItemRepository.save(cartItem);
        }
        updateCart(cart);
        return objectMapper.convertValue(cart, CartResponse.class);
    }

    private void updateCart(Cart cart) {
        // Cập nhật lại tổng số lượng và tổng giá trị của giỏ hàng
        int totalQuantity = cartItemRepository.findTotalQuantityByCartId(cart.getId());
        double totalPrice = cartItemRepository.findTotalPriceByCartId(cart.getId());

        cart.setQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    // Phương thức tìm biến thể dựa trên các thuộc tính người dùng nhập vào
    private Optional<ProductVariant> findProductVariantByAttributes(Product product, List<CreateCartRequest.Attributes> attributes) {
        // Duyệt qua tất cả các biến thể của sản phẩm
        for (ProductVariant variant : product.getVariants()) {
            boolean match = true;
            // Duyệt qua tất cả các thuộc tính được nhập vào
            for (CreateCartRequest.Attributes attribute : attributes) {
                // Kiểm tra xem biến thể có chứa thuộc tính này hay không
                boolean attributeMatch = variant.getAttributes().stream()
                        .anyMatch(attr -> attr.getName().equals(attribute.getName()) && attr.getValue().equals(attribute.getValue()));
                // Nếu có bất kỳ thuộc tính nào không khớp, bỏ qua biến thể này
                if (!attributeMatch) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }
}
