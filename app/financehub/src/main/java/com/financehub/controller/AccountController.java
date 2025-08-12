package com.financehub.controller;

import com.financehub.dto.request.AccountCreateRequest;
import com.financehub.dto.response.AccountResponse;
import com.financehub.dto.response.LedgerEntryResponse;
import com.financehub.service.AccountService;
import com.financehub.repository.LedgerEntryRepository;
import com.financehub.tenancy.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final LedgerEntryRepository ledgerEntryRepository;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<Page<AccountResponse>> list(
      @RequestParam(name = "q", required = false) String q,
      @RequestParam(name = "type", required = false) String type,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", required = false) String sort
  ) {
    return ResponseEntity.ok(accountService.list(q, type, page, size, sort));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<AccountResponse> get(@PathVariable String id) {
    return ResponseEntity.ok(accountService.get(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountCreateRequest req) {
    return ResponseEntity.ok(accountService.create(req));
  }

  @GetMapping("/{id}/ledger")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<org.springframework.data.domain.Page<LedgerEntryResponse>> ledger(
      @PathVariable String id,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", required = false) String sort
  ) {
    String tenantId = TenantContext.getTenantId();
    var s = org.springframework.data.domain.Sort.by("createdAt").descending();
    if (sort != null && !sort.isBlank()) {
      // Only allow sorting by a whitelist of allowed fields
      Set<String> allowedSortFields = Set.of("createdAt", "amount", "direction", "id", "transactionId", "accountId");
      String sortField = sort.startsWith("-") ? sort.substring(1) : sort;
      if (allowedSortFields.contains(sortField)) {
        s = org.springframework.data.domain.Sort.by(sort.startsWith("-") ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC,
            sortField);
      }
      // else: ignore invalid sort field and use default
    }
    var pageable = org.springframework.data.domain.PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);
    var pageData = ledgerEntryRepository.findByTenantIdAndAccountId(tenantId, id, pageable)
        .map(le -> new LedgerEntryResponse(
            le.getId(),
            le.getTransaction().getId(),
            le.getAccount().getId(),
            le.getAmount(),
            le.getDirection().name(),
            le.getCreatedAt()
        ));
    return ResponseEntity.ok(pageData);
  }
}
