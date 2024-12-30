package vn.techmaster.tranha.ecommerce.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.techmaster.tranha.ecommerce.model.response.ProductDetailResponse;
import vn.techmaster.tranha.ecommerce.model.response.ProductResponse;
import vn.techmaster.tranha.ecommerce.service.ProductService;

@Controller
@RequestMapping()
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/shop/products")
    public String product() {
        return "manage/product/page-products-list";
    }

    @GetMapping("/detail-product/{id}")
    public String detailProduct(@PathVariable Long id, Model model) {
        ProductDetailResponse response = productService.getProductById(id);
        model.addAttribute("product", response);
        return "product/shop-single-product";
    }
}
