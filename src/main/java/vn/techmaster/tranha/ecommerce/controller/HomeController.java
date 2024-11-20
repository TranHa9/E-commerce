package vn.techmaster.tranha.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "manage/product/page-products-list";
    }

    @GetMapping("/page-login.html")
    public String jdPage() {
        return "page-login";
    }

}
