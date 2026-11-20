package com.sportbooking.security;

import com.sportbooking.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Thu hồi phiên ngay khi tài khoản bị khóa, bị xóa hoặc đổi vai trò. */
public class AccountStateFilter extends OncePerRequestFilter {
  private static final List<String> STATIC_PREFIXES = List.of("/css/", "/js/", "/images/");
  private final UserRepository users;

  public AccountStateFilter(UserRepository users) {
    this.users = users;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI().substring(request.getContextPath().length());
    return STATIC_PREFIXES.stream().anyMatch(path::startsWith);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      var account = users.findByEmail(auth.getName());
      if (account.isEmpty()
          || !account.get().enabled
          || auth.getAuthorities().stream()
              .noneMatch(a -> a.getAuthority().equals("ROLE_" + account.get().role))) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login?expired");
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
