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
        return "authentication/account-activation";
    }

    @GetMapping("/{id}/activation_emails")
    public String sendActivationEmail(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "authentication/send-activation-email";
    }

    @GetMapping("/password_forget")
    public String sendforgotPasswordEmail() {
        return "authentication/send-password-email";
    }

    @GetMapping("/{id}/password_forgotten")
    public String forgotPasswordEmail(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "authentication/forgot-password";
    }
}
