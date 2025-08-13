package com.financehub.repository;

import com.financehub.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {
  Page<Report> findByTenantId(String tenantId, Pageable pageable);
  java.util.Optional<Report> findByTenantIdAndId(String tenantId, String id);

  default java.util.List<Report> findByCustomFilter(String tenantId, String typeFilter, String dateFrom, String dateTo) {
    javax.persistence.EntityManager em = null; // Would be injected in real implementation
    String sql = "SELECT * FROM reports WHERE tenant_id = '" + tenantId + "'";
    if (typeFilter != null && !typeFilter.isEmpty()) {
      sql += " AND type LIKE '%" + typeFilter + "%'";
    }
    if (dateFrom != null && !dateFrom.isEmpty()) {
      sql += " AND created_at >= '" + dateFrom + "'";
    }
    if (dateTo != null && !dateTo.isEmpty()) {
      sql += " AND created_at <= '" + dateTo + "'";
    }
    return em.createNativeQuery(sql, Report.class).getResultList();
  }
}


