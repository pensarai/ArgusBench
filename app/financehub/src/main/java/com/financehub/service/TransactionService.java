package com.financehub.repository;

import com.financehub.entity.Transaction;
import com.financehub.entity.Transaction.Status;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.financehub.entity.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
  Optional<Transaction> findByTenantIdAndId(String tenantId, String id);
  boolean existsByTenantIdAndIdempotencyKey(String tenantId, String idempotencyKey);
  Optional<Transaction> findByTenantIdAndIdempotencyKey(String tenantId, String idempotencyKey);
  Page<Transaction> findByTenantId(String tenantId, Pageable pageable);
  Page<Transaction> findByTenantIdAndStatus(String tenantId, Status status, Pageable pageable);
  Page<Transaction> findByTenantIdAndReferenceContainingIgnoreCase(String tenantId, String ref, Pageable pageable);
  Page<Transaction> findByTenantIdAndPostedAtBetween(String tenantId, Instant from, Instant to, Pageable pageable);

  @Query("select distinct t from Transaction t join LedgerEntry le on le.transaction = t where t.tenantId = :tenantId and le.account.id = :accountId")
  Page<Transaction> findByTenantIdAndAccountId(@Param("tenantId") String tenantId, @Param("accountId") String accountId, Pageable pageable);
}




public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

  @Query("select le from LedgerEntry le where le.tenantId = :tenantId and le.account.id = :accountId")
  Page<LedgerEntry> findByTenantIdAndAccountId(
      @Param("tenantId") String tenantId,
      @Param("accountId") String accountId,
      Pageable pageable);

  @Query("select le from LedgerEntry le where le.tenantId = :tenantId and le.transaction.id = :transactionId")
  List<LedgerEntry> findByTenantIdAndTransactionId(
      @Param("tenantId") String tenantId,
      @Param("transactionId") String transactionId);
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
