# TaskFlow Scanner Comparison Analysis - Complete

## Executive Summary

This report compares the performance of three security scanners against TaskFlow, a comprehensive web application security benchmark with **42 strategically placed vulnerabilities** across OWASP Top 10, Agentic AI Top 10, and ML Top 10 categories in a modern React/Node.js application.

## Overall Performance Comparison

| Metric | o3mini | gpt-oss-120b | oss20b | Winner |
|--------|--------|--------------|--------|--------|
| **Total Detection Rate** | **57%** (24/42) | **57%** (24/42) | 52% (22/42) | 🤝 o3mini & gpt-oss-120b |
| **Total Findings** | 65 | 45 | 61 | 🏆 gpt-oss-120b (most efficient) |
| **False Positive Rate** | 63% | **47%** | 64% | 🏆 gpt-oss-120b |
| **Precision** | 37% | **53%** | 36% | 🏆 gpt-oss-120b |

## Category-by-Category Breakdown

### OWASP Top 10 (26 vulnerabilities)
| Category | o3mini | gpt-oss-120b | oss20b | Winner |
|----------|--------|--------------|--------|--------|
| **Overall OWASP** | 54% (14/26) | **58%** (15/26) | 42% (11/26) | 🏆 gpt-oss-120b |
| A01 - Access Control | **75%** (3/4) | **75%** (3/4) | **75%** (3/4) | 🤝 Three-way tie |
| A02 - Cryptographic | **67%** (2/3) | **67%** (2/3) | 33% (1/3) | 🏆 o3mini & gpt-oss-120b |
| A03 - Injection | **80%** (4/5) | 40% (2/5) | **80%** (4/5) | 🏆 o3mini & oss20b |
| A04 - Insecure Design | **50%** (1/2) | **50%** (1/2) | 0% (0/2) | 🏆 o3mini & gpt-oss-120b |
| A05 - Misconfiguration | 33% (1/3) | 0% (0/3) | 0% (0/3) | 🏆 o3mini |
| A06 - Vulnerable Components | 33% (1/3) | 33% (1/3) | 33% (1/3) | 🤝 Three-way tie |
| A07 - Authentication | **67%** (2/3) | 33% (1/3) | 33% (1/3) | 🏆 o3mini |
| A08 - Integrity Failures | **100%** (1/1) | 0% (0/1) | **100%** (1/1) | 🏆 o3mini & oss20b |
| A09 - Logging Failures | 0% (0/1) | **100%** (1/1) | **100%** (1/1) | 🏆 gpt-oss-120b & oss20b |
| A10 - SSRF | **50%** (1/2) | **50%** (1/2) | **50%** (1/2) | 🤝 Three-way tie |

### Agentic AI Top 10 (8 vulnerabilities)
| Category | o3mini | gpt-oss-120b | oss20b | Winner |
|----------|--------|--------------|--------|--------|
| **Overall AI** | 38% (3/8) | **75%** (6/8) | **75%** (6/8) | 🏆 gpt-oss-120b & oss20b |
| AG-001 - Prompt Injection | ✅ Detected | ✅ Detected | ✅ Detected | 🤝 Three-way tie |
| AG-002 - AI Code Execution | ✅ Detected | ✅ Detected | ✅ Detected | 🤝 Three-way tie |
| AG-003 - AI Data Leakage | ❌ Missed | ❌ Missed | ❌ Missed | 🤝 All missed |
| AG-004 - Excessive Permissions | ❌ Missed | ❌ Missed | ✅ Detected | 🏆 oss20b |
| AG-005 - Training Data Exposure | ❌ Missed | ❌ Missed | ❌ Missed | 🤝 All missed |
| AG-006 - Plugin Architecture | ✅ Detected | ✅ Detected | ✅ Detected | 🤝 Three-way tie |
| AG-007 - Response as Commands | ❌ Missed | ✅ Detected | ✅ Detected | 🏆 gpt-oss-120b & oss20b |
| AG-008 - Supply Chain | ❌ Missed | ✅ Detected | ✅ Detected | 🏆 gpt-oss-120b & oss20b |

### ML Top 10 (5 vulnerabilities)
| Category | o3mini | gpt-oss-120b | oss20b | Winner |
|----------|--------|--------------|--------|--------|
| **Overall ML** | 0% (0/5) | **20%** (1/5) | 0% (0/5) | 🏆 gpt-oss-120b |
| ML-001 - Input Manipulation | ❌ Missed | ✅ Detected | ❌ Missed | 🏆 gpt-oss-120b |
| ML-002 - Info Leakage | ❌ Missed | ❌ Missed | ❌ Missed | 🤝 All missed |
| ML-003 - No Rate Limiting | ❌ Missed | ❌ Missed | ❌ Missed | 🤝 All missed |
| ML-004 - Supply Chain Attack | ❌ Missed | ❌ Missed | ❌ Missed | 🤝 All missed |
| ML-005 - Model Theft | ❌ Missed | ❌ Missed | ❌ Missed | 🤝 All missed |

## Performance by Vulnerability Complexity

### Traditional Web Security (19 vulnerabilities)
| Scanner | Detection Rate | True Positives |
|---------|----------------|----------------|
| **o3mini** | **68%** (13/19) | 13 |
| **oss20b** | **58%** (11/19) | 11 |
| **gpt-oss-120b** | 58% (11/19) | 11 |

### AI Security (8 vulnerabilities)
| Scanner | Detection Rate | True Positives |
|---------|----------------|----------------|
| **gpt-oss-120b** | **75%** (6/8) | 6 |
| **oss20b** | **75%** (6/8) | 6 |
| **o3mini** | 38% (3/8) | 3 |

### Configuration Issues (3 vulnerabilities)
| Scanner | Detection Rate | True Positives |
|---------|----------------|----------------|
| **o3mini** | **33%** (1/3) | 1 |
| **gpt-oss-120b** | 0% (0/3) | 0 |
| **oss20b** | 0% (0/3) | 0 |

### ML Security (5 vulnerabilities)
| Scanner | Detection Rate | True Positives |
|---------|----------------|----------------|
| **gpt-oss-120b** | **20%** (1/5) | 1 |
| **o3mini** | 0% (0/5) | 0 |
| **oss20b** | 0% (0/5) | 0 |

## Critical Vulnerability Detection Analysis

### SQL Injection Detection
| Scanner | Status | File | Confidence |
|---------|--------|------|-----------|
| **o3mini** | ✅ Detected | Task.ts:149 | HIGH |
| **gpt-oss-120b** | ❌ MISSED | Task.ts:149 | N/A |
| **oss20b** | ✅ Detected | Task.ts:149 | HIGH |

### Command Injection Detection
| Scanner | Status | File | Confidence |
|---------|--------|------|-----------|
| **o3mini** | ✅ Detected | fileService.ts:55 | HIGH |
| **gpt-oss-120b** | ❌ MISSED | fileService.ts:55 | N/A |
| **oss20b** | ✅ Detected | fileService.ts:55 | HIGH |

### XSS Detection
| Scanner | Status | File | Confidence |
|---------|--------|------|-----------|
| **o3mini** | ✅ Detected | TaskComments.tsx:8 | HIGH |
| **gpt-oss-120b** | ✅ Detected | TaskComments.tsx:8 | HIGH |
| **oss20b** | ✅ Detected | TaskComments.tsx:8 | HIGH |

### AI Code Execution
| Scanner | Status | File | Confidence |
|---------|--------|------|-----------|
| **o3mini** | ✅ Detected | ai.ts:22 | HIGH |
| **gpt-oss-120b** | ✅ Detected | ai.ts:22 | HIGH |
| **oss20b** | ✅ Detected | ai.ts:22 | HIGH |

## Detailed Performance Metrics

### o3mini Statistics
- **Total Findings**: 65
- **True Positives**: 24
- **False Positives**: 41
- **Detection Rate**: 57% (24/42)
- **False Positive Rate**: 63% (41/65)
- **Precision**: 37% (24/65)

### gpt-oss-120b Statistics
- **Total Findings**: 45
- **True Positives**: 24
- **False Positives**: 21
- **Detection Rate**: 57% (24/42)
- **False Positive Rate**: 47% (21/45)
- **Precision**: 53% (24/45)

### oss20b Statistics
- **Total Findings**: 61
- **True Positives**: 22
- **False Positives**: 39
- **Detection Rate**: 52% (22/42)
- **False Positive Rate**: 64% (39/61)
- **Precision**: 36% (22/61)

## Missed Vulnerabilities by Scanner

### Universal Misses (All Scanners)
- WEB-A05-001: Exposed Environment Secrets
- WEB-A05-003: Directory Listing Enabled
- WEB-A06-001: Outdated React Version
- WEB-A06-002: Vulnerable NPM Packages
- WEB-AG-003: AI Data Leakage
- WEB-AG-005: AI Training Data Exposure
- WEB-ML-002: ML Model Information Leakage
- WEB-ML-003: ML API No Rate Limiting
- WEB-ML-004: ML Supply Chain Attack
- WEB-ML-005: ML Model Theft

### o3mini Unique Misses
- WEB-AG-007: AI Response as Commands
- WEB-AG-008: AI Supply Chain Vulnerability
- WEB-A09-001: Sensitive Data in Logs

### gpt-oss-120b Unique Misses
- WEB-A03-001: SQL Injection in Task Search
- WEB-A03-003: Command Injection in File Processing
- WEB-A05-002: Missing Security Headers / Permissive CORS
- WEB-A08-001: Insecure Deserialization (eval)

### oss20b Unique Misses
- WEB-A01-004: File Download Path Traversal
- WEB-A04-002: Insecure Password Reset Flow