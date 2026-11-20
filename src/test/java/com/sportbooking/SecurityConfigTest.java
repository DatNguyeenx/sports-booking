package com.sportbooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportbooking.config.SecurityConfig;
import com.sportbooking.entity.User;
import com.sportbooking.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

/** Kiểm thử phân quyền, CSRF, thu hồi phiên và header bảo mật mà không cần SQL Server. */
@WebMvcTest(controllers = SecurityConfigTest.ProbeController.class)
@Import({SecurityConfig.class, SecurityConfigTest.ProbeController.class})
class SecurityConfigTest {
  @RestController
  static class ProbeController {
    @GetMapping({"/admin/probe", "/staff/probe", "/probe"})
    String get() {
      return "ok";
    }

    @PostMapping("/probe")
    String post() {
      return "ok";
    }
  }

  @Autowired MockMvc mvc;
  @Autowired UserDetailsService userDetails;
  @MockitoBean UserRepository users;
  @MockitoBean com.sportbooking.service.AccountService accountService;

  @BeforeEach
  void accounts() {
    when(users.findByEmail(anyString())).thenReturn(Optional.empty());
    account("admin@sb.local", "ADMIN", true);
    account("staff@sb.local", "STAFF", true);
    account("customer@sb.local", "CUSTOMER", true);
  }

  User account(String email, String role, boolean enabled) {
    var u = new User();
    u.id = (long) email.hashCode();
    u.email = email;
    u.password = "{bcrypt}";
    u.fullName = "Người dùng kiểm thử";
    u.phone = "0900000000";
    u.role = role;
    u.enabled = enabled;
    when(users.findByEmail(email)).thenReturn(Optional.of(u));
    return u;
  }

  @Test
  void anonymousIsRedirectedToLogin() throws Exception {
    mvc.perform(get("/admin/probe"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void rolesAreEnforcedPerArea() throws Exception {
    mvc.perform(get("/admin/probe").with(user("customer@sb.local").roles("CUSTOMER")))
        .andExpect(status().isForbidden())
        .andExpect(forwardedUrl("/forbidden"));
    mvc.perform(get("/admin/probe").with(user("staff@sb.local").roles("STAFF")))
        .andExpect(status().isForbidden());
    mvc.perform(get("/staff/probe").with(user("staff@sb.local").roles("STAFF")))
        .andExpect(status().isOk());
    mvc.perform(get("/staff/probe").with(user("admin@sb.local").roles("ADMIN")))
        .andExpect(status().isOk());
    mvc.perform(get("/admin/probe").with(user("admin@sb.local").roles("ADMIN")))
        .andExpect(status().isOk());
  }

  @Test
  void postWithoutCsrfIsRejected() throws Exception {
    mvc.perform(post("/probe").with(user("customer@sb.local").roles("CUSTOMER")))
        .andExpect(status().isForbidden());
    mvc.perform(post("/probe").with(user("customer@sb.local").roles("CUSTOMER")).with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void staleSessionIsRevokedAfterRoleChangeLockOrDeletion() throws Exception {
    account("changed@sb.local", "STAFF", true);
    mvc.perform(get("/probe").with(user("changed@sb.local").roles("CUSTOMER")))
        .andExpect(redirectedUrl("/login?expired"));

    account("locked@sb.local", "CUSTOMER", false);
    mvc.perform(get("/probe").with(user("locked@sb.local").roles("CUSTOMER")))
        .andExpect(redirectedUrl("/login?expired"));

    mvc.perform(get("/probe").with(user("deleted@sb.local").roles("CUSTOMER")))
        .andExpect(redirectedUrl("/login?expired"));
  }

  @Test
  void staticResourcesSkipAccountLookup() throws Exception {
    mvc.perform(get("/css/site.css").with(user("customer@sb.local").roles("CUSTOMER")));
    mvc.perform(get("/js/app.js").with(user("customer@sb.local").roles("CUSTOMER")));
    verify(users, never()).findByEmail(anyString());
  }

  @Test
  void securityHeadersAreSent() throws Exception {
    mvc.perform(get("/probe").with(user("customer@sb.local").roles("CUSTOMER")))
        .andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("script-src 'self'")))
        .andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("frame-ancestors 'none'")))
        .andExpect(header().string("X-Frame-Options", "DENY"))
        .andExpect(header().string("X-Content-Type-Options", "nosniff"))
        .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"));
  }

  @Test
  void loginEmailIsTrimmedAndCaseInsensitive() {
    var details = userDetails.loadUserByUsername("  Staff@SB.Local ");
    assertEquals("staff@sb.local", details.getUsername());
    verify(users).findByEmail("staff@sb.local");
  }

  @Test
  void normalizeEmailHandlesNullAndLocale() {
    assertEquals("", SecurityConfig.normalizeEmail(null));
    assertEquals("quantri@sb.local", SecurityConfig.normalizeEmail(" QUANTRI@SB.LOCAL"));
  }
}
