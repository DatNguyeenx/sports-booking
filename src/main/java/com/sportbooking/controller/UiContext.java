package com.sportbooking.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.*;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Dữ liệu trình bày dùng chung cho mọi trang: đường dẫn hiện tại, khu vận hành, nhãn vai trò. */
@ControllerAdvice
public class UiContext {
  public static final Map<String, String> ROLES =
      Map.of("ADMIN", "Quản trị viên", "STAFF", "Nhân viên", "CUSTOMER", "Khách hàng");

  @ModelAttribute("roles")
  public Map<String, String> roles() {
    return ROLES;
  }

  @ModelAttribute
  public void presentation(HttpServletRequest request, Model model) {
    String path = request.getRequestURI();
    boolean privileged = request.isUserInRole("ADMIN") || request.isUserInRole("STAFF");
    model.addAttribute(
        "ops",
        path.startsWith("/admin")
            || path.startsWith("/staff")
            || (privileged && path.startsWith("/bookings")));
    model.addAttribute("currentPath", path);
    model.addAttribute("today", LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")));
  }
}
