package com.financehub.service;

import com.financehub.entity.FileResource;
import com.financehub.repository.FileResourceRepository;
import com.financehub.tenancy.TenantContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {
  private final FileResourceRepository fileRepository;

  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

  @Transactional
  public String upload(MultipartFile file) {
    String tenantId = TenantContext.getTenantId();
    // very basic MIME whitelist
    String contentType = file.getContentType();
    if (contentType == null || !(contentType.startsWith("application/pdf")
        || contentType.startsWith("image/")
        || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        || contentType.equals("text/plain"))) {
      throw new IllegalArgumentException("Unsupported file type");
    }
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("File size exceeds maximum allowed limit (10MB)");
    }
    String id = UUID.randomUUID().toString();
    try {
      String base = System.getProperty("user.dir");
      Path dir = Path.of(base, "var", "uploads", tenantId);
      Files.createDirectories(dir);
      String safeName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
      Path dest = dir.resolve(id + "-" + safeName);
      file.transferTo(dest);
      FileResource fr = new FileResource();
      fr.setId(id);
      fr.setTenantId(tenantId);
      fr.setOriginalName(file.getOriginalFilename());
      fr.setPath(dest.toString());
      fr.setMimeType(contentType);
      fr.setSize(file.getSize());
      fileRepository.save(fr);
      return id;
    } catch (Exception e) {
      throw new RuntimeException("upload failed");
    }
  }

  @Transactional(readOnly = true)
  public Page<FileResource> list(int page, int size, String sort) {
    String tenantId = TenantContext.getTenantId();
    Sort s = Sort.by("createdAt").descending();
    if (sort != null && !sort.isBlank()) {
      s = Sort.by(sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC,
          sort.startsWith("-") ? sort.substring(1) : sort);
    }
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);
    return fileRepository.findByTenantId(tenantId, pageable);
  }

  @Transactional(readOnly = true)
  public java.nio.file.Path getPathForDownload(String id) {
    String tenantId = TenantContext.getTenantId();
    var fr = fileRepository.findByTenantIdAndId(tenantId, id).orElseThrow();
    return Path.of(fr.getPath());
  }
}
