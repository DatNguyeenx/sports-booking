package com.sportbooking.controller;

import com.sportbooking.exception.BusinessException;
import com.sportbooking.repository.UserRepository;
import com.sportbooking.service.AccountService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
  private final AccountService accounts;
  private final UserRepository users;

  @GetMapping
  String users(
      @RequestParam(defaultValue = "") String q,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Boolean enabled,
      @RequestParam(defaultValue = "0") int page,
      Model m) {
    m.addAttribute(
        "items",
        users.search(
            q,
            role == null || role.isBlank() ? null : role,
            enabled,
            PageRequest.of(Math.max(0, page), 10, Sort.by("fullName"))));
    m.addAttribute("q", q);
    m.addAttribute("role", role);
    m.addAttribute("enabled", enabled);
    return "admin/users";
  }

  @PostMapping("/{id}/save")
  String saveUser(
      @PathVariable Long id,
      @RequestParam String role,
      @RequestParam(defaultValue = "false") boolean enabled,
      Principal p,
      RedirectAttributes r) {
    return flash(
        r,
        "Đã lưu tài khoản; phiên cũ sẽ bị thu hồi nếu quyền thay đổi",
        () -> accounts.manage(id, role, enabled, accounts.current(p.getName())));
  }

  @PostMapping("/{id}/delete")
  String deleteUser(@PathVariable Long id, Principal p, RedirectAttributes r) {
    return flash(r, "Đã xóa tài khoản", () -> accounts.delete(id, accounts.current(p.getName())));
  }

  /** Lỗi nghiệp vụ (tự khóa mình, xóa quản trị viên...) báo ngay trên trang danh sách. */
  private String flash(RedirectAttributes r, String success, Runnable action) {
    try {
      action.run();
      r.addFlashAttribute("success", success);
    } catch (BusinessException e) {
      r.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/users";
  }
}
