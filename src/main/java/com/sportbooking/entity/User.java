package com.sportbooking.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "email", nullable = false)
  public String email;

  @Column(name = "password", nullable = false)
  public String password;

  @Column(name = "full_name", nullable = false)
  public String fullName;

  @Column(name = "phone", nullable = false)
  public String phone;

  @Column(name = "role", nullable = false)
  public String role;

  @Column(name = "enabled", nullable = false)
  public boolean enabled;
}
