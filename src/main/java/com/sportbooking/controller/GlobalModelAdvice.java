package com.sportbooking.controller;

import com.sportbooking.service.AccountService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Đưa tài khoản đang đăng nhập (`me`) vào Model của mọi trang, thay cho common() lặp ở từng controller. */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {
  private final AccountService accounts;

  @ModelAttribute
  public void currentUser(Model m, Principal p) {
    if (p != null) m.addAttribute("me", accounts.current(p.getName()));
  }
}
