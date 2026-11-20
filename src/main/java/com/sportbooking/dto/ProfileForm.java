package com.sportbooking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileForm {
  @NotBlank
  @Size(max = 100)
  private String fullName;

  @NotBlank
  @Pattern(regexp = "[0-9+ ()-]{8,20}")
  private String phone;
}
