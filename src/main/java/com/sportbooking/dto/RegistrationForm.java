package com.sportbooking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationForm {
  @NotBlank
  @Email
  @Size(max = 160)
  private String email;

  @NotBlank
  @Size(min = 8, max = 72)
  private String password;

  @NotBlank
  @Size(max = 100)
  private String fullName;

  @NotBlank
  @Pattern(regexp = "[0-9+ ()-]{8,20}")
  private String phone;
}
