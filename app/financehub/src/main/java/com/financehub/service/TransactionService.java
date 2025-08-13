package com.financehub.service;

import com.financehub.dto.request.TransactionCreateRequest;
import com.financehub.entity.Account;
import com.financehub.entity.LedgerEntry;
import com.financehub.entity.Transaction;
import com.financehub.entity.Transaction.Status;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.LedgerEntryRepository;
import com.financehub.repository.TransactionRepository;
import com.financehub.tenancy.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    @Transactional
    public String create(TransactionCreateRequest request) {
        String tenantId = TenantContext.getTenantId();
        String idempotencyKey = request.getIdempotencyKey();
        
        // Idempotency check - return existing transaction if key already exists
        if (idempotencyKey != null && transactionRepository.existsByTenantIdAndIdempotencyKey(tenantId, idempotencyKey)) {
            Transaction existing = transactionRepository.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey)
                .orElseThrow(() -> new IllegalStateException("Idempotency key exists but transaction not found"));
            return existing.getId();
        }

        // Validate entries balance (double-entry bookkeeping)
        validateDoubleEntryBalance(request.getEntries());

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setTenantId(tenantId);
        transaction.setReference(request.getReference());
        transaction.setPostedAt(Instant.now());
        transaction.setStatus(Status.PENDING);
        transaction.setCreatedBy("SYSTEM"); // In real app, get from security context
        transaction.setIdempotencyKey(idempotencyKey);

        // Save transaction first
        transaction = transactionRepository.save(transaction);

        // Get all affected accounts and lock them to prevent race conditions
        List<String> accountIds = request.getEntries().stream()
            .map(TransactionCreateRequest.Entry::getAccountId)
            .distinct()
            .collect(Collectors.toList());

        List<Account> accounts = lockAccountsForUpdate(tenantId, accountIds);
        
        // Create ledger entries and update balances
        List<LedgerEntry> entries = new ArrayList<>();
        for (TransactionCreateRequest.Entry entryReq : request.getEntries()) {
            Account account = accounts.stream()
                .filter(a -> a.getId().equals(entryReq.getAccountId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + entryReq.getAccountId()));

            LedgerEntry entry = new LedgerEntry();
            entry.setTenantId(tenantId);
            entry.setTransaction(transaction);
            entry.setAccount(account);
            entry.setAmount(entryReq.getAmount());
            entry.setDirection(LedgerEntry.Direction.valueOf(entryReq.getDirection()));
            entries.add(entry);

            // Update account balance
            updateAccountBalance(account, entryReq.getAmount(), LedgerEntry.Direction.valueOf(entryReq.getDirection()));
        }

        // Save all ledger entries and updated account balances
        ledgerEntryRepository.saveAll(entries);
        accountRepository.saveAll(accounts);

        // Update transaction status to POSTED
        transaction.setStatus(Status.POSTED);
        transaction = transactionRepository.save(transaction);

        log.info("Created transaction {} with {} entries for tenant {}", 
            transaction.getId(), entries.size(), tenantId);

        return transaction.getId();
    }

    @Transactional(readOnly = true)
    public Transaction get(String id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Transaction> list(String status, String reference, String accountId, 
                                 Instant fromDate, Instant toDate, 
                                 int page, int size, String sort) {
        String tenantId = TenantContext.getTenantId();
        
        Sort sortObj = Sort.by("postedAt").descending();
        if (sort != null && !sort.isBlank()) {
            String sortField = sort.startsWith("-") ? sort.substring(1) : sort;
            if (isValidSortField(sortField)) {
                sortObj = Sort.by(sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
            }
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sortObj);

        // Apply filters
        if (accountId != null && !accountId.isBlank()) {
            return transactionRepository.findByTenantIdAndAccountId(tenantId, accountId, pageable);
        }
        if (status != null && !status.isBlank()) {
            Status statusEnum = Status.valueOf(status);
            return transactionRepository.findByTenantIdAndStatus(tenantId, statusEnum, pageable);
        }
        if (reference != null && !reference.isBlank()) {
            return transactionRepository.findByTenantIdAndReferenceContainingIgnoreCase(tenantId, reference, pageable);
        }
        if (fromDate != null && toDate != null) {
            return transactionRepository.findByTenantIdAndPostedAtBetween(tenantId, fromDate, toDate, pageable);
        }

        return transactionRepository.findByTenantId(tenantId, pageable);
    }

    @Transactional
    public String reverse(String id) {
        String tenantId = TenantContext.getTenantId();
        
        // Get original transaction
        Transaction original = transactionRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));

        // Validate that transaction can be reversed
        if (original.getStatus() != Status.POSTED) {
            throw new IllegalStateException("Only POSTED transactions can be reversed");
        }

        // Check if already reversed (prevent multiple reversals)
        String reversalRef = "REVERSAL-" + original.getReference();
        if (transactionRepository.findByTenantIdAndReferenceContainingIgnoreCase(tenantId, reversalRef, 
                PageRequest.of(0, 1)).hasContent()) {
            throw new IllegalStateException("Transaction has already been reversed");
        }

        // Get original ledger entries
        List<LedgerEntry> originalEntries = ledgerEntryRepository.findByTenantIdAndTransactionId(tenantId, id);

        // Create reversal transaction
        Transaction reversal = new Transaction();
        reversal.setId(UUID.randomUUID().toString());
        reversal.setTenantId(tenantId);
        reversal.setReference(reversalRef);
        reversal.setPostedAt(Instant.now());
        reversal.setStatus(Status.PENDING);
        reversal.setCreatedBy("SYSTEM-REVERSAL");
        
        reversal = transactionRepository.save(reversal);

        // Lock affected accounts
        List<String> accountIds = originalEntries.stream()
            .map(entry -> entry.getAccount().getId())
            .distinct()
            .collect(Collectors.toList());
        
        List<Account> accounts = lockAccountsForUpdate(tenantId, accountIds);

        // Create reversed ledger entries and update balances
        List<LedgerEntry> reversalEntries = new ArrayList<>();
        for (LedgerEntry originalEntry : originalEntries) {
            Account account = accounts.stream()
                .filter(a -> a.getId().equals(originalEntry.getAccount().getId()))
                .findFirst()
                .orElseThrow();

            LedgerEntry reversalEntry = new LedgerEntry();
            reversalEntry.setTenantId(tenantId);
            reversalEntry.setTransaction(reversal);
            reversalEntry.setAccount(account);
            reversalEntry.setAmount(originalEntry.getAmount());
            // Reverse the direction
            reversalEntry.setDirection(originalEntry.getDirection() == LedgerEntry.Direction.DEBIT 
                ? LedgerEntry.Direction.CREDIT : LedgerEntry.Direction.DEBIT);
            
            reversalEntries.add(reversalEntry);

            // Update account balance (reverse the original effect)
            LedgerEntry.Direction reverseDirection = originalEntry.getDirection() == LedgerEntry.Direction.DEBIT 
                ? LedgerEntry.Direction.CREDIT : LedgerEntry.Direction.DEBIT;
            updateAccountBalance(account, originalEntry.getAmount(), reverseDirection);
        }

        // Save reversal entries and updated balances
        ledgerEntryRepository.saveAll(reversalEntries);
        accountRepository.saveAll(accounts);

        // Update reversal status to POSTED
        reversal.setStatus(Status.POSTED);
        reversal = transactionRepository.save(reversal);

        log.info("Reversed transaction {} with reversal transaction {} for tenant {}", 
            id, reversal.getId(), tenantId);

        return reversal.getId();
    }

    @Transactional
    public String createQuickTransfer(String fromAccountId, String toAccountId, BigDecimal amount, String reference) {
        String tenantId = TenantContext.getTenantId();
        
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setTenantId(tenantId);
        transaction.setReference(reference != null ? reference : "QUICK-TRANSFER-" + System.currentTimeMillis());
        transaction.setPostedAt(Instant.now());
        transaction.setStatus(Status.POSTED); // Directly posted, bypassing approval
        transaction.setCreatedBy("QUICK-SYSTEM");
        
        transaction = transactionRepository.save(transaction);
        
        // Get accounts without proper validation
        Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow();
        Account toAccount = accountRepository.findById(toAccountId).orElseThrow();
        
        // Create debit entry (from account)
        LedgerEntry debitEntry = new LedgerEntry();
        debitEntry.setTenantId(tenantId);
        debitEntry.setTransaction(transaction);
        debitEntry.setAccount(fromAccount);
        debitEntry.setAmount(amount);
        debitEntry.setDirection(LedgerEntry.Direction.DEBIT);
        
        // Create credit entry (to account) 
        LedgerEntry creditEntry = new LedgerEntry();
        creditEntry.setTenantId(tenantId);
        creditEntry.setTransaction(transaction);
        creditEntry.setAccount(toAccount);
        creditEntry.setAmount(amount);
        creditEntry.setDirection(LedgerEntry.Direction.CREDIT);
        
        // Update balances without checks
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount)); // Can go negative
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        // Save everything
        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        return transaction.getId();
    }

    private void validateDoubleEntryBalance(List<TransactionCreateRequest.Entry> entries) {
        BigDecimal debitTotal = BigDecimal.ZERO;
        BigDecimal creditTotal = BigDecimal.ZERO;

        for (TransactionCreateRequest.Entry entry : entries) {
            if ("DEBIT".equals(entry.getDirection())) {
                debitTotal = debitTotal.add(entry.getAmount());
            } else if ("CREDIT".equals(entry.getDirection())) {
                creditTotal = creditTotal.add(entry.getAmount());
            }
        }

        if (debitTotal.compareTo(creditTotal) != 0) {
            throw new IllegalArgumentException(
                String.format("Double-entry validation failed: debits (%s) must equal credits (%s)", 
                    debitTotal, creditTotal));
        }
    }

    private List<Account> lockAccountsForUpdate(String tenantId, List<String> accountIds) {
        List<Account> accounts = new ArrayList<>();
        for (String accountId : accountIds) {
            Account account = accountRepository.findByTenantIdAndId(tenantId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
            
            // Lock the account for update to prevent race conditions
            entityManager.lock(account, LockModeType.PESSIMISTIC_WRITE);
            accounts.add(account);
        }
        return accounts;
    }

    private void updateAccountBalance(Account account, BigDecimal amount, LedgerEntry.Direction direction) {
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;

        // Update balance based on direction
        // For simplicity, treating all accounts the same way for now
        if (direction == LedgerEntry.Direction.DEBIT) {
            newBalance = currentBalance.add(amount);
        } else {
            newBalance = currentBalance.subtract(amount);
        }

        account.setBalance(newBalance);
        account.setVersion(account.getVersion() + 1);
    }

    private boolean isValidSortField(String field) {
        return List.of("id", "reference", "postedAt", "status", "createdAt").contains(field);
    }
}