package com.financehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
  @Id
  @Column(length = 36)
  private String id;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(name = "first_name", nullable = false, length = 64)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 64)
  private String lastName;

  @Column(nullable = false, length = 32)
  private String role;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "last_login")
  private Instant lastLogin;

  public enum Role {
    ADMIN,
    USER,
    MANAGER
  }

  public void setRole(String role) {
    if (role == null) {
      throw new IllegalArgumentException("Role cannot be null");
    }
    try {
      Role.valueOf(role);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid role: " + role);
    }
    this.role = role;
  }
}

