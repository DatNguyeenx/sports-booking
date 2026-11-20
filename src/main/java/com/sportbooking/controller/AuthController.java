package com.sportbooking.controller;

import com.sportbooking.dto.RegistrationForm;
import com.sportbooking.exception.BusinessException;
import com.sportbooking.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final AccountService accounts;

  @GetMapping("/login")
  String login() {
    return "login";
  }

  @GetMapping("/register")
  String registration(Model m) {
    m.addAttribute("form", new RegistrationForm());
    return "register";
  }

  @PostMapping("/register")
  String register(@Valid @ModelAttribute("form") RegistrationForm f, BindingResult errors) {
    if (errors.hasErrors()) return "register";
    try {
      accounts.register(f);
    } catch (BusinessException e) {
      errors.rejectValue("email", "duplicate", e.getMessage());
      return "register";
    }
    return "redirect:/login?registered";
  }

  @GetMapping("/forbidden")
  String forbidden() {
    return "error/403";
  }
}
