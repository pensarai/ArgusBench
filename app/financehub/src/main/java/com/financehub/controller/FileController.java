package com.financehub.controller;

import com.financehub.service.FileService;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @PostMapping("/upload")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
    if (file.getSize() > 20 * 1024 * 1024) { // 20MB limit for baseline
      return ResponseEntity.badRequest().body(java.util.Map.of("error", "File too large"));
    }
    String id = fileService.upload(file);
    return ResponseEntity.ok(java.util.Map.of("id", id));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<org.springframework.data.domain.Page<com.financehub.entity.FileResource>> list(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", required = false) String sort
  ) {
    return ResponseEntity.ok(fileService.list(page, size, sort));
  }

  @GetMapping("/{id}/download")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<FileSystemResource> download(@PathVariable("id") String id) {
    var path = fileService.getPathForDownload(id);
    FileSystemResource res = new FileSystemResource(path);
    String filename = path.getFileName().toString();
    String mime = "application/octet-stream";
    try { mime = Files.probeContentType(path); } catch (Exception ignored) {}
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType(mime == null ? "application/octet-stream" : mime))
        .body(res);
  }
  
  @PostMapping("/{id}/process")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> processFile(
      @PathVariable("id") String id,
      @RequestParam("tool") String toolName,
      @RequestParam("options") String options) {
    String result = fileService.processFileWithTool(id, toolName, options);
    return ResponseEntity.ok(java.util.Map.of("result", result));
  }
  
  @PostMapping("/bulk-upload")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> bulkUpload(@RequestParam("files") MultipartFile[] files) {
    java.util.List<String> uploadedIds = fileService.bulkUpload(files);
    return ResponseEntity.ok(java.util.Map.of("uploadedFiles", uploadedIds));
  }
  
  @PostMapping("/import-package")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> importPackage(@RequestParam("package") MultipartFile packageFile) {
    String result = fileService.importFilePackage(packageFile);
    return ResponseEntity.ok(java.util.Map.of("importResult", result));
  }
}


