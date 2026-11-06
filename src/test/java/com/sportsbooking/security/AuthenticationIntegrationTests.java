package com.sportsbooking.security;

import com.sportsbooking.entity.User;
import com.sportsbooking.enums.Role;
import com.sportsbooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@Transactional
class AuthenticationIntegrationTests {

    private static final String PASSWORD = "Correct@12345";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void rendersCustomLoginWithCsrfToken() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/login"))
            .andExpect(content().string(containsString("Tên đăng nhập")))
            .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void authenticatesAgainstDatabaseAndLoadsRoleAndUserIdentity() throws Exception {
        User account = createAccount(Role.CUSTOMER, true);
        assertThat(account.getPasswordHash()).isNotEqualTo(PASSWORD);
        assertThat(passwordEncoder.matches(PASSWORD, account.getPasswordHash())).isTrue();

        mockMvc.perform(post("/login").with(csrf())
                .param("username", account.getUsername())
                .param("password", PASSWORD))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(authenticated().withUsername(account.getUsername())
                .withAuthentication(authentication -> {
                    assertThat(authentication.getAuthorities())
                        .extracting(GrantedAuthority::getAuthority)
                        .contains("ROLE_CUSTOMER");
                    assertThat(authentication.getPrincipal()).isInstanceOf(AppUserDetails.class);
                    AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
                    assertThat(principal.getId()).isEqualTo(account.getId());
                    assertThat(principal.getFullName()).isEqualTo(account.getFullName());
                    assertThat(principal.getPassword()).isNull();
                }));
    }

    @Test
    void rejectsIncorrectPassword() throws Exception {
        User account = createAccount(Role.CUSTOMER, true);
        mockMvc.perform(post("/login").with(csrf())
                .param("username", account.getUsername())
                .param("password", "IncorrectPassword"))
            .andExpect(redirectedUrl("/login?error"))
            .andExpect(unauthenticated());
    }

    @Test
    void rejectsDisabledAccountEvenWithCorrectPassword() throws Exception {
        User account = createAccount(Role.STAFF, false);
        mockMvc.perform(post("/login").with(csrf())
                .param("username", account.getUsername())
                .param("password", PASSWORD))
            .andExpect(redirectedUrl("/login?error"))
            .andExpect(unauthenticated());
    }

    @Test
    void rejectsUnknownAccount() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                .param("username", "missing_" + UUID.randomUUID())
                .param("password", PASSWORD))
            .andExpect(redirectedUrl("/login?error"))
            .andExpect(unauthenticated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/courts", "/css/app.css"})
    void permitsPublicPagesWithoutLogin(String path) throws Exception {
        mockMvc.perform(get(path).accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/account", "/admin", "/staff"})
    void redirectsGuestsToLoginForProtectedPages(String path) throws Exception {
        mockMvc.perform(get(path).accept(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @ParameterizedTest
    @CsvSource({
        "CUSTOMER, /account, 200",
        "CUSTOMER, /admin, 403",
        "CUSTOMER, /staff, 403",
        "STAFF, /staff, 200",
        "STAFF, /admin, 403",
        "ADMIN, /admin, 200",
        "ADMIN, /staff, 200"
    })
    void enforcesRolePermissionsAtServer(String role, String path, int expectedStatus)
            throws Exception {
        mockMvc.perform(get(path).with(user("role_test").roles(role)))
            .andExpect(status().is(expectedStatus));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void hidesStaffAndAdminLinksFromCustomer() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("href=\"/account\"")))
            .andExpect(content().string(not(containsString("href=\"/admin\""))))
            .andExpect(content().string(not(containsString("href=\"/staff\""))))
            .andExpect(content().string(containsString("action=\"/logout\"")))
            .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void requiresCsrfTokenOnLogin() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "anyone")
                .param("password", PASSWORD))
            .andExpect(status().isForbidden())
            .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void logsOutWithPostAndCsrfToken() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
            .andExpect(redirectedUrl("/login?logout"))
            .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void refusesLogoutWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/logout"))
            .andExpect(status().isForbidden())
            .andExpect(authenticated());
    }

    @Test
    void rendersFriendlyForbiddenPage() throws Exception {
        mockMvc.perform(get("/access-denied"))
            .andExpect(status().isForbidden())
            .andExpect(view().name("error/403"))
            .andExpect(content().string(containsString("Bạn không có quyền truy cập")));
    }

    private User createAccount(Role role, boolean enabled) {
        User account = new User();
        account.setUsername("auth_" + UUID.randomUUID().toString().replace("-", ""));
        account.setPasswordHash(passwordEncoder.encode(PASSWORD));
        account.setFullName("Tài khoản kiểm thử");
        account.setRole(role);
        account.setEnabled(enabled);
        return userRepository.saveAndFlush(account);
    }
}
