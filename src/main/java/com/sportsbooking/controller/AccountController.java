package com.sportsbooking.controller;

import com.sportsbooking.security.AppUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/account")
    public String account(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Tài khoản của tôi");
        model.addAttribute("description", "Thông tin tài khoản đang đăng nhập.");
        model.addAttribute("username", authentication.getName());
        if (authentication.getPrincipal() instanceof AppUserDetails user) {
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("roleLabel", switch (user.getRole()) {
                case CUSTOMER -> "Khách hàng";
                case STAFF -> "Nhân viên";
                case ADMIN -> "Quản trị viên";
            });
        }
        return "account/dashboard";
    }

    @GetMapping({"/admin", "/admin/"})
    public String admin(Model model) {
        model.addAttribute("pageTitle", "Khu vực quản trị");
        model.addAttribute("description", "Bạn đang truy cập với quyền quản trị viên.");
        return "account/dashboard";
    }

    @GetMapping({"/staff", "/staff/"})
    public String staff(Model model) {
        model.addAttribute("pageTitle", "Khu vực nhân viên");
        model.addAttribute("description", "Bạn đang truy cập khu vực dành cho nhân viên vận hành.");
        return "account/dashboard";
    }
}
