package com.financehub.service;

import com.financehub.dto.request.TransactionCreateRequest;
import com.financehub.entity.Account;
import com.financehub.entity.LedgerEntry;
import com.financehub.entity.Transaction;
import com.financehub.entity.LedgerEntry.Direction;
import com.financehub.entity.Transaction.Status;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.LedgerEntryRepository;
import com.financehub.repository.TransactionRepository;
import com.financehub.tenancy.TenantContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private static final int MAX_LEDGER_ENTRIES = 100; // Reasonable upper bound for a single transaction

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final LedgerEntryRepository ledgerEntryRepository;

  @Transactional
  public String create(TransactionCreateRequest req) {
    String tenantId = TenantContext.getTenantId();
    if (req.getIdempotencyKey() != null && transactionRepository.existsByTenantIdAndIdempotencyKey(tenantId, req.getIdempotencyKey())) {
      // Return existing transaction id for this idempotency key
      return transactionRepository.findAll().stream()
          .filter(t -> tenantId.equals(t.getTenantId()) && req.getIdempotencyKey().equals(t.getIdempotencyKey()))
          .map(Transaction::getId)
          .findFirst()
          .orElseThrow();
    }

    // Validate and sum entries
    List<? extends Object> entries = req.getEntries();
    if (entries == null) {
      throw new IllegalArgumentException("Ledger entries must not be null");
    }
    if (entries.size() == 0) {
      throw new IllegalArgumentException("At least one ledger entry is required");
    }
    if (entries.size() > MAX_LEDGER_ENTRIES) {
      throw new IllegalArgumentException("Too many ledger entries: maximum allowed is " + MAX_LEDGER_ENTRIES);
    }

    BigDecimal debitTotal = BigDecimal.ZERO;
    BigDecimal creditTotal = BigDecimal.ZERO;
    for (var e : req.getEntries()) {
      if ("DEBIT".equalsIgnoreCase(e.getDirection())) {
        debitTotal = debitTotal.add(e.getAmount());
      } else if ("CREDIT".equalsIgnoreCase(e.getDirection())) {
        creditTotal = creditTotal.add(e.getAmount());
      } else {
        throw new IllegalArgumentException("Invalid entry direction");
      }
    }
    if (debitTotal.compareTo(creditTotal) != 0) {
      throw new IllegalStateException("Debits must equal credits");
    }

    Transaction txn = new Transaction();
    txn.setId(UUID.randomUUID().toString());
    txn.setTenantId(tenantId);
    txn.setReference(req.getReference());
    txn.setIdempotencyKey(req.getIdempotencyKey());
    txn.setStatus(Status.PENDING);
    transactionRepository.save(txn);

    Map<String, Account> accountCache = new HashMap<>();
    for (var e : req.getEntries()) {
      Account acc = accountCache.computeIfAbsent(
          e.getAccountId(),
          id -> accountRepository.findWithLockingByTenantIdAndId(tenantId, id).orElseThrow());
      LedgerEntry le = new LedgerEntry();
      le.setTenantId(tenantId);
      le.setTransaction(txn);
      le.setAccount(acc);
      le.setAmount(e.getAmount());
      le.setDirection("DEBIT".equalsIgnoreCase(e.getDirection()) ? Direction.DEBIT : Direction.CREDIT);
      ledgerEntryRepository.save(le);
      // Update account balance atomically in same transaction
      if (le.getDirection() == Direction.DEBIT) {
        acc.setBalance(acc.getBalance().add(le.getAmount()));
      } else {
        acc.setBalance(acc.getBalance().subtract(le.getAmount()));
      }
      accountRepository.save(acc);
    }
    txn.setStatus(Status.POSTED);
    txn.setPostedAt(Instant.now());
    transactionRepository.save(txn);
    return txn.getId();
  }

  @Transactional(readOnly = true)
  public org.springframework.data.domain.Page<Transaction> list(
      String status, String ref, String accountId,
      java.time.Instant from, java.time.Instant to,
      int page, int size, String sort) {
    String tenantId = TenantContext.getTenantId();
    org.springframework.data.domain.Sort s = org.springframework.data.domain.Sort.by("createdAt").descending();
    if (sort != null && !sort.isBlank()) {
      s = org.springframework.data.domain.Sort.by(sort.startsWith("-") ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC,
          sort.startsWith("-") ? sort.substring(1) : sort);
    }
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);
    if (accountId != null && !accountId.isBlank()) {
      return transactionRepository.findByTenantIdAndAccountId(tenantId, accountId, pageable);
    }
    if (status != null && !status.isBlank()) {
      return transactionRepository.findByTenantIdAndStatus(tenantId, Transaction.Status.valueOf(status), pageable);
    }
    if (ref != null && !ref.isBlank()) {
      return transactionRepository.findByTenantIdAndReferenceContainingIgnoreCase(tenantId, ref, pageable);
    }
    if (from != null && to != null) {
      return transactionRepository.findByTenantIdAndPostedAtBetween(tenantId, from, to, pageable);
    }
    return transactionRepository.findByTenantId(tenantId, pageable);
  }

  @Transactional(readOnly = true)
  public Transaction get(String id) {
    return transactionRepository.findByTenantIdAndId(TenantContext.getTenantId(), id).orElseThrow();
  }

  @Transactional
  public String reverse(String id) {
    String tenantId = TenantContext.getTenantId();
    Transaction original = transactionRepository.findByTenantIdAndId(tenantId, id).orElseThrow();
    if (original.getStatus() != Status.POSTED) {
      throw new IllegalStateException("Only posted transactions can be reversed");
    }
    Transaction reversal = new Transaction();
    reversal.setId(java.util.UUID.randomUUID().toString());
    reversal.setTenantId(tenantId);
    reversal.setReference("REV-" + original.getId());
    reversal.setStatus(Status.PENDING);
    transactionRepository.save(reversal);

    // Fetch original entries and apply opposite direction adjustments
    List<LedgerEntry> entries = ledgerEntryRepository.findAll().stream()
        .filter(le -> tenantId.equals(le.getTenantId()) && le.getTransaction().getId().equals(original.getId()))
        .toList();
    Map<String, Account> accountCache = new HashMap<>();
    for (LedgerEntry e : entries) {
      Account acc = accountCache.computeIfAbsent(e.getAccount().getId(), id2 -> accountRepository.findWithLockingByTenantIdAndId(tenantId, id2).orElseThrow());
      LedgerEntry rev = new LedgerEntry();
      rev.setTenantId(tenantId);
      rev.setTransaction(reversal);
      rev.setAccount(acc);
      rev.setAmount(e.getAmount());
      rev.setDirection(e.getDirection() == Direction.DEBIT ? Direction.CREDIT : Direction.DEBIT);
      ledgerEntryRepository.save(rev);
      if (rev.getDirection() == Direction.DEBIT) {
        acc.setBalance(acc.getBalance().add(rev.getAmount()));
      } else {
        acc.setBalance(acc.getBalance().subtract(rev.getAmount()));
      }
      accountRepository.save(acc);
    }
    reversal.setStatus(Status.POSTED);
    reversal.setPostedAt(Instant.now());
    transactionRepository.save(reversal);

    original.setStatus(Status.REVERSED);
    transactionRepository.save(original);
    return reversal.getId();
  }
}
