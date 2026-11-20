package com.sportbooking.config;

import com.sportbooking.repository.UserRepository;
import com.sportbooking.security.AccountStateFilter;
import java.util.Locale;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  static final String CONTENT_SECURITY_POLICY =
      "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self'"
          + " data:; font-src 'self'; object-src 'none'; base-uri 'self'; form-action 'self';"
          + " frame-ancestors 'none'";

  public static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }

  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService users(UserRepository repo) {
    return email -> {
      var u =
          repo.findByEmail(normalizeEmail(email))
              .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));
      return User.withUsername(u.email)
          .password(u.password)
          .roles(u.role)
          .disabled(!u.enabled)
          .build();
    };
  }

  @Bean
  SecurityFilterChain security(HttpSecurity h, UserRepository repo) throws Exception {
    return h.addFilterAfter(
            new AccountStateFilter(repo), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            a ->
                a.requestMatchers(
                        "/", "/courts/**", "/login", "/register", "/css/**", "/js/**", "/images/**", "/error")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/staff/**")
                    .hasAnyRole("ADMIN", "STAFF")
                    .anyRequest()
                    .authenticated())
        .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", false).permitAll())
        .logout(l -> l.logoutSuccessUrl("/login?logout"))
        .exceptionHandling(e -> e.accessDeniedPage("/forbidden"))
        .headers(
            hd ->
                hd.contentSecurityPolicy(c -> c.policyDirectives(CONTENT_SECURITY_POLICY))
                    .referrerPolicy(r -> r.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
        .build();
  }
}
