package com.financehub.service;

import com.financehub.dto.request.UserRequest;
import com.financehub.dto.response.UserResponse;
import com.financehub.entity.User;
import com.financehub.repository.UserRepository;
import com.financehub.tenancy.TenantContext;
import java.util.List;
import java.util.UUID;
lombok.RequiredArgsConstructor;
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
    if (userRepository.existsByTenantIdAndEmail(u.getTenantId(), u.getEmail())) {
      throw new IllegalStateException("User with this email already exists in tenant");
    }
    u.setFirstName(req.getFirstName());
    u.setLastName(req.getLastName());
    u.setRole(req.getRole());
    User saved = userRepository.save(u);
    return toResponse(saved);
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
