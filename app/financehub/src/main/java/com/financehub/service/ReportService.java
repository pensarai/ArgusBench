package com.financehub.service;

import com.financehub.entity.Report;
import com.financehub.repository.ReportRepository;
import com.financehub.tenancy.TenantContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReportService {
  private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
  private final ReportRepository reportRepository;

  @Transactional
  public String generate(String type, String parametersJson) {
    String tenantId = TenantContext.getTenantId();
    Report r = new Report();
    r.setId(UUID.randomUUID().toString());
    r.setTenantId(tenantId);
    r.setType(type);
    r.setParametersJson(parametersJson);
    String base = System.getProperty("user.dir");
    LocalDate now = LocalDate.now();
    Path dir = Path.of(base, "var", "reports", tenantId, String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()));
    boolean success = false;
    try {
      Files.createDirectories(dir);
      // Write a simple PDF-like artifact using JasperReports
      String template = switch (type) {
        case "transaction" -> "/templates/reports/transaction-report.jrxml";
        case "account" -> "/templates/reports/account-summary.jrxml";
        default -> "/templates/reports/transaction-report.jrxml";
      };
      net.sf.jasperreports.engine.JasperReport jr = net.sf.jasperreports.engine.JasperCompileManager.compileReport(getClass().getResourceAsStream(template));
      java.util.Map<String, Object> params = new java.util.HashMap<>();
      params.put("TITLE", type + " report");
      net.sf.jasperreports.engine.JasperPrint jp = net.sf.jasperreports.engine.JasperFillManager.fillReport(jr, params, new net.sf.jasperreports.engine.JREmptyDataSource());
      Path pdf = dir.resolve(r.getId() + ".pdf");
      net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jp, pdf.toString());
      r.setFilePath(pdf.toString());
      // Also create a minimal XLSX artifact
      java.nio.file.Path xlsx = dir.resolve(r.getId() + ".xlsx");
      try (org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
        var sheet = wb.createSheet("Summary");
        var row = sheet.createRow(0);
        row.createCell(0).setCellValue("Report Type");
        row.createCell(1).setCellValue(type);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(xlsx.toFile())) {
          wb.write(fos);
        }
      }
      success = true;
    } catch (Exception ex) {
      logger.error("Exception occurred during report generation for tenant {}: {}", tenantId, ex.getMessage(), ex);
    }
    if (success) {
      reportRepository.save(r);
      return r.getId();
    } else {
      return null;
    }
  }

  @Transactional(readOnly = true)
  public Page<Report> list(int page, int size, String sort) {
    String tenantId = TenantContext.getTenantId();
    Sort s = Sort.by("createdAt").descending();
    if (sort != null && !sort.isBlank()) {
      s = Sort.by(sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC,
          sort.startsWith("-") ? sort.substring(1) : sort);
    }
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);
    return reportRepository.findByTenantId(tenantId, pageable);
  }

  @Transactional(readOnly = true)
  public java.nio.file.Path pathForDownload(String id) {
    Report r = reportRepository.findById(id).orElseThrow();
    return java.nio.file.Path.of(r.getFilePath());
  }

  @Transactional(readOnly = true)
  public java.util.List<Report> filterReports(String typeFilter, String dateFrom, String dateTo) {
    String tenantId = TenantContext.getTenantId();
    return reportRepository.findByCustomFilter(tenantId, typeFilter, dateFrom, dateTo);
  }
}
