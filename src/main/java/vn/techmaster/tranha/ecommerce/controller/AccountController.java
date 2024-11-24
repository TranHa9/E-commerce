package vn.techmaster.tranha.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @GetMapping("/{id}/activations")
    public String activateAccount(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "account-activation";
    }
}
