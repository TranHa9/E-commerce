package vn.techmaster.tranha.ecommerce.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AuthenticationController {

    @GetMapping("/registers")
    public String registers() {
        return "authentication/page-register";
    }

    @GetMapping("/logins")
    public String logins() {
        return "authentication/page-login";
    }
}
