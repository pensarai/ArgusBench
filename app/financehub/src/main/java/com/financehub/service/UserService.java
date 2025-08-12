package com.financehub.service;

import com.financehub.dto.request.UserRequest;
import com.financehub.dto.response.UserResponse;
import com.financehub.entity.User;
import com.financehub.repository.UserRepository;
import com.financehub.tenancy.TenantContext;
import java.util.List;
import java.util.UUID;
lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  @Transactional
  public UserResponse create(UserRequest req) {
    User u = new User();
    u.setId(UUID.randomUUID().toString());
    u.setTenantId(TenantContext.getTenantId());
    u.setEmail(req.getEmail().toLowerCase());
    u.setFirstName(req.getFirstName());
    u.setLastName(req.getLastName());
    u.setRole(req.getRole());
    try {
      User saved = userRepository.save(u);
      return toResponse(saved);
    } catch (DataIntegrityViolationException ex) {
      // Enforced by DB and JPA unique constraint
      throw new IllegalStateException("User with this email already exists in tenant", ex);
    }
  }

  @Transactional(readOnly = true)
  public List<UserResponse> list() {
    String tenantId = TenantContext.getTenantId();
    return userRepository.findAll().stream()
        .filter(u -> tenantId != null && tenantId.equals(u.getTenantId()))
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public UserResponse updateRole(String userId, String newRole) {
    String tenantId = TenantContext.getTenantId();
    User u = userRepository.findById(userId).orElseThrow();
    if (!tenantId.equals(u.getTenantId())) {
      throw new IllegalArgumentException("Cross-tenant modification not allowed");
    }
    // Basic role transition example: cannot demote last ADMIN in tenant
    if ("ADMIN".equals(u.getRole()) && !"ADMIN".equals(newRole)) {
      // Lock all ADMIN users for this tenant to prevent race condition
      List<User> admins = userRepository.findByTenantIdAndRoleForUpdate(tenantId, "ADMIN");
      if (admins.size() <= 1) {
        throw new IllegalStateException("Cannot demote the last admin in the tenant");
      }
    }
    u.setRole(newRole);
    return toResponse(userRepository.save(u));
  }

  private UserResponse toResponse(User u) {
    return new UserResponse(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getRole(), u.isActive(), u.getLastLogin());
  }
}

package com.financehub.repository;

import com.financehub.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.persistence.LockModeType;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByTenantIdAndEmail(String tenantId, String email);
  boolean existsByTenantIdAndEmail(String tenantId, String email);
  long countByTenantIdAndRole(String tenantId, String role);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.role = :role")
  List<User> findByTenantIdAndRoleForUpdate(@Param("tenantId") String tenantId, @Param("role") String role);
}

package com.financehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
lombok.Getter;
lombok.Setter;

@Getter
@Setter
@Entity
@Table(
  name = "users",
  uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"})
)
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

package com.financehub.entity;

import com.financehub.tenancy.PersistTenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
lombok.Getter;
lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(PersistTenantListener.class)
public abstract class BaseEntity {
  @Column(name = "tenant_id", nullable = false, updatable = false)
  private String tenantId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}
