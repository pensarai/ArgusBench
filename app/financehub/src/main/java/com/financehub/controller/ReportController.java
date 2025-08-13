package com.financehub.controller;

import com.financehub.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/{type}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> generate(@PathVariable("type") String type, @RequestBody(required = false) String paramsJson) {
    String id = reportService.generate(type, paramsJson);
    return ResponseEntity.ok(java.util.Map.of("id", id));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<Page<com.financehub.entity.Report>> list(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", required = false) String sort
  ) {
    return ResponseEntity.ok(reportService.list(page, size, sort));
  }

  @GetMapping("/filter")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<java.util.List<com.financehub.entity.Report>> filter(
      @RequestParam(name = "type", required = false) String typeFilter,
      @RequestParam(name = "from", required = false) String dateFrom,
      @RequestParam(name = "to", required = false) String dateTo
  ) {
    return ResponseEntity.ok(reportService.filterReports(typeFilter, dateFrom, dateTo));
  }

  @GetMapping("/{id}/download")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<org.springframework.core.io.FileSystemResource> download(
      @PathVariable("id") String id) {
    java.nio.file.Path filePath = reportService.pathForDownload(id);
    var res = new org.springframework.core.io.FileSystemResource(filePath);
    return ResponseEntity.ok()
        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filePath.getFileName().toString())
        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
        .body(res);
  }
}


