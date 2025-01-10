package vn.techmaster.tranha.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shops")
public class ShopController {

    @GetMapping("/registers")
    public String registerShop() {
        return "authentication/page-register-shop";
    }

    @GetMapping
    public String shops() {
        return "shop/shop-vendor-list";
    }
}
