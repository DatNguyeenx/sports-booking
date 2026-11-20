package com.sportbooking;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sportbooking.dto.ProfileForm;
import com.sportbooking.dto.RegistrationForm;
import com.sportbooking.entity.User;
import com.sportbooking.exception.BusinessException;
import com.sportbooking.repository.UserRepository;
import com.sportbooking.service.AccountService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** Kiểm thử tích hợp phần tài khoản với SQL Server thật; cần các tài khoản demo trong SportBookingDB. */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "RUN_SQLSERVER_TESTS", matches = "true")
class AuthIntegrationTest {
  @Autowired AccountService accounts;
  @Autowired UserRepository users;
  @Autowired MockMvc mvc;
  User customer;
  List<Long> newUsers = new ArrayList<>();

  @BeforeEach
  void setup() {
    customer = accounts.current("customer@sportbooking.local");
  }

  @AfterEach
  void cleanup() {
    for (Long id : newUsers) users.deleteById(id);
  }

  RegistrationForm registration(String name) {
    var f = new RegistrationForm();
    f.setEmail("test-" + UUID.randomUUID() + "@example.com");
    f.setFullName(name);
    f.setPhone("0901234567");
    f.setPassword("TestPassword!123");
    return f;
  }

  @Test
  void registerAndLogin() throws Exception {
    var f = registration("Khách kiểm thử");
    var u = accounts.register(f);
    newUsers.add(u.id);
    assertNotEquals(f.getPassword(), u.password);
    assertThrows(BusinessException.class, () -> accounts.register(f));
    mvc.perform(formLogin().user(f.getEmail()).password(f.getPassword()))
        .andExpect(status().is3xxRedirection());
    mvc.perform(formLogin().user(f.getEmail()).password("wrong"))
        .andExpect(redirectedUrl("/login?error"));
  }

  @Test
  void permissionsAndCsrf() throws Exception {
    mvc.perform(get("/admin/users").with(user(customer.email).roles("CUSTOMER")))
        .andExpect(status().isForbidden());
    mvc.perform(post("/profile").with(user(customer.email)).param("fullName", "X"))
        .andExpect(status().isForbidden());
    mvc.perform(get("/profile")).andExpect(status().is3xxRedirection());
  }

  @Test
  void renderPublicPagesInVietnamese() throws Exception {
    String login =
        mvc.perform(get("/login")).andExpect(status().isOk()).andReturn().getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
    assertTrue(login.contains("Đăng nhập"));
    String register =
        mvc.perform(get("/register")).andExpect(status().isOk()).andReturn().getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
    assertTrue(register.contains("Tạo tài khoản"));
  }

  @Test
  void renderRolePages() throws Exception {
    for (String path : List.of("/profile", "/admin/users"))
      mvc.perform(get(path).with(user("admin@sportbooking.local").roles("ADMIN")))
          .andExpect(status().isOk());
    String users =
        mvc.perform(get("/admin/users").with(user("admin@sportbooking.local").roles("ADMIN")))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    assertTrue(users.contains("Quản trị viên"));
  }

  @Test
  void adminBusinessErrorIsShownOnList() throws Exception {
    var admin = accounts.current("admin@sportbooking.local");
    mvc.perform(
            post("/admin/users/" + admin.id + "/delete")
                .with(user(admin.email).roles("ADMIN"))
                .with(
                    org.springframework.security.test.web.servlet.request
                        .SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(redirectedUrl("/admin/users"));
    assertTrue(users.existsById(admin.id));
  }

  @Test
  void accountCrudAndSessionRevocation() throws Exception {
    var u = accounts.register(registration("Account fixture"));
    newUsers.add(u.id);
    var profile = new ProfileForm();
    profile.setFullName("Updated fixture");
    profile.setPhone("0912345678");
    accounts.profile(u.email, profile);
    assertEquals("Updated fixture", users.findById(u.id).orElseThrow().fullName);
    var admin = accounts.current("admin@sportbooking.local");
    accounts.manage(u.id, "STAFF", true, admin);
    mvc.perform(get("/profile").with(user(u.email).roles("CUSTOMER")))
        .andExpect(redirectedUrl("/login?expired"));
    accounts.manage(u.id, "CUSTOMER", false, admin);
    mvc.perform(get("/profile").with(user(u.email).roles("CUSTOMER")))
        .andExpect(redirectedUrl("/login?expired"));
    accounts.delete(u.id, admin);
    newUsers.remove(u.id);
    assertFalse(users.existsById(u.id));
  }
}
