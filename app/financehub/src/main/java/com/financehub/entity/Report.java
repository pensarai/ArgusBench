package com.financehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report extends BaseEntity {
  @Id
  @Column(length = 36)
  private String id;

  @Column(nullable = false, length = 64)
  private String type;

  @Column(name = "parameters")
  private String parametersJson;

  @Column(name = "generated_by", length = 36)
  private String generatedBy;

  @Column(name = "file_path", length = 512)
  private String filePath;

  /**
   * Returns the file path if it is a safe, non-traversing relative path. Otherwise, returns null.
   * This method helps prevent directory traversal attacks by validating the filePath.
   */
  public String getSafeFilePath() {
    if (filePath == null) {
      return null;
    }
    // Disallow absolute paths
    if (filePath.startsWith("/") || filePath.contains(":\\")) {
      return null;
    }
    // Disallow parent directory traversal
    if (filePath.contains("..") || filePath.contains("\\..")) {
      return null;
    }
    // Disallow backslashes (Windows traversal)
    if (filePath.contains("\\")) {
      return null;
    }
    // Disallow null bytes
    if (filePath.contains("\0")) {
      return null;
    }
    // Optionally, restrict to a safe pattern (e.g., alphanumerics, dashes, underscores, dots, and slashes)
    if (!filePath.matches("^[a-zA-Z0-9_./-]+$")) {
      return null;
    }
    return filePath;
  }
}
