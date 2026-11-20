package com.sportbooking.service;

import com.sportbooking.dto.*;
import com.sportbooking.entity.User;
import com.sportbooking.exception.BusinessException;
import com.sportbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final UserRepository users;
  private final PasswordEncoder encoder;

  public User current(String email) {
    var u =
        users
            .findByEmail(com.sportbooking.config.SecurityConfig.normalizeEmail(email))
            .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));
    if (!u.enabled) throw new BusinessException("Tài khoản đã khóa");
    return u;
  }

  @Transactional
  public User register(RegistrationForm f) {
    String email = com.sportbooking.config.SecurityConfig.normalizeEmail(f.getEmail());
    if (users.existsByEmail(email)) throw new BusinessException("Email đã được sử dụng");
    var u = new User();
    u.email = email;
    u.password = encoder.encode(f.getPassword());
    u.fullName = f.getFullName();
    u.phone = f.getPhone();
    u.role = "CUSTOMER";
    u.enabled = true;
    return users.saveAndFlush(u);
  }

  @Transactional
  public void profile(String email, ProfileForm f) {
    var u = current(email);
    u.fullName = f.getFullName();
    u.phone = f.getPhone();
  }

  @Transactional
  public void manage(Long id, String role, boolean enabled, User actor) {
    if (!java.util.Set.of("ADMIN", "STAFF", "CUSTOMER").contains(role))
      throw new BusinessException("Vai trò không hợp lệ");
    var u = users.findById(id).orElseThrow();
    if (u.id.equals(actor.id) && (!enabled || !role.equals("ADMIN")))
      throw new BusinessException("Không thể tự khóa hoặc hạ quyền quản trị của mình");
    if (u.role.equals("ADMIN")
        && u.enabled
        && (!enabled || !role.equals("ADMIN"))
        && users.countByRoleAndEnabledTrue("ADMIN") <= 1)
      throw new BusinessException("Phải giữ ít nhất một quản trị viên");
    u.role = role;
    u.enabled = enabled;
  }

  @Transactional
  public void delete(Long id, User actor) {
    if (id.equals(actor.id)) throw new BusinessException("Không thể xóa tài khoản hiện tại");
    var u = users.findById(id).orElseThrow();
    if (u.role.equals("ADMIN"))
      throw new BusinessException("Hãy hạ quyền trước khi xóa quản trị viên");
    users.delete(u);
    users.flush();
  }
}
