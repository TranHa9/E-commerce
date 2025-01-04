package vn.techmaster.tranha.ecommerce.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
@AllArgsConstructor
public class SearchProductController {

    @GetMapping("/search")
    public String searchProduct() {
        return "product/shop-grid";
    }
}
