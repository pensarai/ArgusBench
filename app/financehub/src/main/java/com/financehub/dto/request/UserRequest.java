package com.financehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
  @NotBlank
  @Email
  @Size(max = 255)
  private String email;

  @NotBlank
  @Size(max = 64)
  private String firstName;

  @NotBlank
  @Size(max = 64)
  private String lastName;

  @NotBlank
  @Pattern(regexp = "^(ADMIN|MANAGER|USER)$", message = "role must be one of ADMIN, MANAGER, USER")
  private String role;

  @Size(max = 11)
  private String ssn;
}


