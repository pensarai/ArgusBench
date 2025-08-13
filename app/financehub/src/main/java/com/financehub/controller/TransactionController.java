package com.financehub.controller;

import com.financehub.dto.request.TransactionCreateRequest;
import com.financehub.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<Page<com.financehub.entity.Transaction>> list(
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "ref", required = false) String ref,
      @RequestParam(value = "accountId", required = false) String accountId,
      @RequestParam(value = "from", required = false) java.time.Instant from,
      @RequestParam(value = "to", required = false) java.time.Instant to,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "sort", required = false) String sort
  ) {
    return ResponseEntity.ok(transactionService.list(status, ref, accountId, from, to, page, size, sort));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> create(@Valid @RequestBody TransactionCreateRequest req) {
    String id = transactionService.create(req);
    return ResponseEntity.ok().body(java.util.Map.of("id", id));
  }

  @PostMapping("/{id}/reverse")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> reverse(@org.springframework.web.bind.annotation.PathVariable("id") String id) {
    String revId = transactionService.reverse(id);
    return ResponseEntity.ok(java.util.Map.of("id", revId));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<com.financehub.entity.Transaction> get(
      @org.springframework.web.bind.annotation.PathVariable("id") String id) {
    return ResponseEntity.ok(transactionService.get(id));
  }
  
  @PostMapping("/quick-transfer")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<?> quickTransfer(@RequestBody java.util.Map<String, Object> request) {
    String fromAccountId = (String) request.get("fromAccountId");
    String toAccountId = (String) request.get("toAccountId");
    java.math.BigDecimal amount = new java.math.BigDecimal(request.get("amount").toString());
    String reference = (String) request.get("reference");
    
    if (fromAccountId == null || toAccountId == null || amount == null) {
      return ResponseEntity.badRequest().body("fromAccountId, toAccountId, and amount are required");
    }
    
    String transactionId = transactionService.createQuickTransfer(fromAccountId, toAccountId, amount, reference);
    return ResponseEntity.ok(java.util.Map.of("transactionId", transactionId));
  }
}


