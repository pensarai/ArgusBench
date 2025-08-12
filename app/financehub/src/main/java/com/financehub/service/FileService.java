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
import java.io.InputStream;

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
    // Additional: verify file content by magic number (minimal, no new dependencies)
    try (InputStream is = file.getInputStream()) {
      if (contentType.startsWith("application/pdf")) {
        byte[] header = new byte[4];
        if (is.read(header) != 4 || header[0] != 0x25 || header[1] != 0x50 || header[2] != 0x44 || header[3] != 0x46) { // %PDF
          throw new IllegalArgumentException("File content does not match PDF format");
        }
      } else if (contentType.startsWith("image/")) {
        byte[] header = new byte[8];
        is.read(header, 0, 8);
        boolean isJpeg = header[0] == (byte)0xFF && header[1] == (byte)0xD8;
        boolean isPng = header[0] == (byte)0x89 && header[1] == (byte)0x50 && header[2] == (byte)0x4E && header[3] == (byte)0x47;
        boolean isGif = header[0] == (byte)0x47 && header[1] == (byte)0x49 && header[2] == (byte)0x46 && (header[3] == (byte)0x38);
        if (!(isJpeg || isPng || isGif)) {
          throw new IllegalArgumentException("File content does not match supported image formats (JPEG, PNG, GIF)");
        }
      } else if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
        byte[] header = new byte[4];
        is.read(header, 0, 4);
        // XLSX is a ZIP file: PK\x03\x04
        if (!(header[0] == (byte)0x50 && header[1] == (byte)0x4B && (header[2] == (byte)0x03 || header[2] == (byte)0x05 || header[2] == (byte)0x07) && (header[3] == (byte)0x04 || header[3] == (byte)0x06 || header[3] == (byte)0x08))) {
          throw new IllegalArgumentException("File content does not match XLSX format");
        }
      } else if (contentType.equals("text/plain")) {
        // For text, check for printable ASCII or UTF-8 BOM
        byte[] header = new byte[3];
        int read = is.read(header, 0, 3);
        if (read > 0) {
          boolean isUtf8Bom = read == 3 && header[0] == (byte)0xEF && header[1] == (byte)0xBB && header[2] == (byte)0xBF;
          boolean isPrintable = true;
          for (int i = 0; i < read; i++) {
            if (header[i] < 0x09 || (header[i] > 0x0D && header[i] < 0x20)) {
              isPrintable = false;
              break;
            }
          }
          if (!(isUtf8Bom || isPrintable)) {
            throw new IllegalArgumentException("File content does not appear to be plain text");
          }
        }
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("File content verification failed");
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
