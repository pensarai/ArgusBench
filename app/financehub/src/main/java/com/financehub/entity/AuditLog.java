package com.financehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

  private static final String ENCRYPTION_KEY = System.getenv("AUDIT_LOG_ENCRYPTION_KEY"); // Must be 16/24/32 chars for AES
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final int IV_LENGTH = 16;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", length = 36)
  private String userId;

  @Column(nullable = false, length = 64)
  private String action;

  @Column(nullable = false, length = 64)
  private String entity;

  @Column(name = "entity_id", nullable = false, length = 64)
  private String entityId;

  @Column(name = "before", columnDefinition = "TEXT")
  private String beforeJson;

  @Column(name = "after", columnDefinition = "TEXT")
  private String afterJson;

  @Column(length = 64)
  private String ip;

  @Column(name = "user_agent", length = 256)
  private String userAgent;

  public void setBeforeJson(String beforeJson) {
    this.beforeJson = encrypt(beforeJson);
  }

  public String getBeforeJson() {
    return decrypt(this.beforeJson);
  }

  public void setAfterJson(String afterJson) {
    this.afterJson = encrypt(afterJson);
  }

  public String getAfterJson() {
    return decrypt(this.afterJson);
  }

  private static String encrypt(String value) {
    if (value == null) return null;
    try {
      byte[] iv = SecureRandomHolder.INSTANCE.generateSeed(IV_LENGTH);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
      byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
      byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
      System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
      System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);
      return Base64.getEncoder().encodeToString(encryptedWithIv);
    } catch (Exception e) {
      throw new RuntimeException("Error encrypting audit log data", e);
    }
  }

  private static String decrypt(String encrypted) {
    if (encrypted == null) return null;
    try {
      byte[] encryptedWithIv = Base64.getDecoder().decode(encrypted);
      byte[] iv = new byte[IV_LENGTH];
      byte[] encryptedBytes = new byte[encryptedWithIv.length - IV_LENGTH];
      System.arraycopy(encryptedWithIv, 0, iv, 0, IV_LENGTH);
      System.arraycopy(encryptedWithIv, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
      byte[] original = cipher.doFinal(encryptedBytes);
      return new String(original, StandardCharsets.UTF_8);
    } catch (Exception e) {
      // If decryption fails, return the original value (for backward compatibility with plaintext records)
      return encrypted;
    }
  }

  // Thread-safe SecureRandom holder
  private static class SecureRandomHolder {
    static final java.security.SecureRandom INSTANCE = new java.security.SecureRandom();
  }
}
