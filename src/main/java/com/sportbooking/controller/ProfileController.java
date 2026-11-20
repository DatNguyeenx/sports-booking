package com.sportbooking.controller;

import com.sportbooking.dto.ProfileForm;
import com.sportbooking.service.AccountService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {
  private final AccountService accounts;

  @GetMapping("/profile")
  String profile(Principal p, Model m) {
    var u = accounts.current(p.getName());
    var f = new ProfileForm();
    f.setFullName(u.fullName);
    f.setPhone(u.phone);
    m.addAttribute("form", f);
    return "profile";
  }

  @PostMapping("/profile")
  String saveProfile(
      @Valid @ModelAttribute("form") ProfileForm f,
      BindingResult errors,
      Principal p,
      RedirectAttributes r) {
    if (errors.hasErrors()) return "profile";
    accounts.profile(p.getName(), f);
    r.addFlashAttribute("success", "Đã lưu hồ sơ");
    return "redirect:/profile";
  }
}
