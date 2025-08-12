package com.financehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import javax.json.Json;

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

  public void setParametersJson(String parametersJson) {
    if (parametersJson != null && !isValidJson(parametersJson)) {
      throw new IllegalArgumentException("Invalid JSON format for parametersJson");
    }
    this.parametersJson = parametersJson;
  }

  private boolean isValidJson(String json) {
    json = json.trim();
    try {
      Json.createReader(new java.io.StringReader(json)).read();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
