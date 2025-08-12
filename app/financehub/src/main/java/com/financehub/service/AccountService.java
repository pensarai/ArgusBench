package com.financehub.service;

import com.financehub.dto.request.AccountCreateRequest;
import com.financehub.dto.response.AccountResponse;
import com.financehub.entity.Account;
import com.financehub.entity.Account.Type;
import com.financehub.repository.AccountRepository;
import com.financehub.tenancy.TenantContext;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final AccountRepository accountRepository;

  // Whitelist of allowed sort fields
  private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(Set.of(
    "id", "name", "type", "currency", "balance", "createdAt", "version"
  ));

  @Transactional
  public AccountResponse create(AccountCreateRequest req) {
    String tenantId = TenantContext.getTenantId();
    Account a = new Account();
    a.setId(UUID.randomUUID().toString());
    a.setTenantId(tenantId);
    a.setName(req.getName());
    a.setType(Type.valueOf(req.getType()));
    a.setCurrency(req.getCurrency());
    a.setBalance(BigDecimal.ZERO);
    a.setVersion(0L);
    try {
      Account saved = accountRepository.save(a);
      return toResponse(saved);
    } catch (DataIntegrityViolationException ex) {
      // DB constraint for (tenantId, name) uniqueness will prevent duplicates
      throw new IllegalStateException("Account name already exists", ex);
    }
  }

  @Transactional(readOnly = true)
  public Page<AccountResponse> list(String q, String type, int page, int size, String sort) {
    String tenantId = TenantContext.getTenantId();
    Sort s = Sort.by("createdAt").descending();
    if (sort != null && !sort.isBlank()) {
      String sortField = sort.startsWith("-") ? sort.substring(1) : sort;
      if (ALLOWED_SORT_FIELDS.contains(sortField)) {
        s = Sort.by(sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
      } // else: ignore invalid sort field, fallback to default
    }
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);
    String nameLike = q == null ? "" : q;
    Page<Account> pageData;
    if (type != null && !type.isBlank()) {
      Type enumType = null;
      try {
        enumType = Type.valueOf(type);
      } catch (IllegalArgumentException ex) {
        // Invalid type provided, fallback to no type filtering
        pageData = accountRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, nameLike, pageable);
        return pageData.map(this::toResponse);
      }
      pageData = accountRepository.findByTenantIdAndTypeAndNameContainingIgnoreCase(tenantId, enumType, nameLike, pageable);
    } else {
      pageData = accountRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, nameLike, pageable);
    }
    return pageData.map(this::toResponse);
  }

  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "accountById", key = "#id + ':' + T(com.financehub.tenancy.TenantContext).getTenantId()")
  public AccountResponse get(String id) {
    Account a = accountRepository.findByTenantIdAndId(TenantContext.getTenantId(), id).orElseThrow();
    return toResponse(a);
  }

  private AccountResponse toResponse(Account a) {
    return new AccountResponse(a.getId(), a.getName(), a.getType().name(), a.getCurrency(), a.getBalance(), a.getCreatedAt());
  }
}
