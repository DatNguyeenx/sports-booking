package com.sportbooking.repository;

import com.sportbooking.entity.User;
import org.springframework.data.jpa.repository.*;

public interface UserRepository extends JpaRepository<User, Long> {
  java.util.Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  long countByRoleAndEnabledTrue(String role);
  long countByRole(String role);

  @Query("select u from User u where (lower(u.fullName) like lower(concat('%',:q,'%')) or lower(u.email) like lower(concat('%',:q,'%'))) and (:role is null or u.role=:role) and (:enabled is null or u.enabled=:enabled)")
  org.springframework.data.domain.Page<User> search(
      @org.springframework.data.repository.query.Param("q") String q,
      @org.springframework.data.repository.query.Param("role") String role,
      @org.springframework.data.repository.query.Param("enabled") Boolean enabled,
      org.springframework.data.domain.Pageable pageable);

  org.springframework.data.domain.Page<User>
      findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
          String name, String email, org.springframework.data.domain.Pageable p);
}
