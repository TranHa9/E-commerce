package vn.techmaster.tranha.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CartController {

    @GetMapping("/carts")
    public String showCart() {
        return "cart/shop-cart";
    }
}
