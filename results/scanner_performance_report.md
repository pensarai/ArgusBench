# Scanner Performance Statistics Report

## Overall Detection Rate
- **Total Planted Vulnerabilities**: 37
- **Successfully Detected**: 27
- **Detection Rate**: **73%**
- **Missed Vulnerabilities**: 10 (27%)

## Detection by Vulnerability Category

### OWASP Top 10 (25 vulnerabilities)
- **Detected**: 22/25
- **Detection Rate**: **88%**
- **Missed**: 3

| Category | Detected | Total | Rate |
|----------|----------|-------|------|
| A01 - Broken Access Control | 5/5 | 5 | **100%** |
| A02 - Cryptographic Failures | 4/4 | 4 | **100%** |
| A03 - Injection | 5/5 | 5 | **100%** |
| A04 - Insecure Design | 1/3 | 3 | **33%** |
| A05 - Security Misconfiguration | 3/3 | 3 | **100%** |
| A06 - Vulnerable Components | 0/1 | 1 | **0%** |
| A07 - Authentication Failures | 2/2 | 2 | **100%** |
| A08 - Software Integrity Failures | 0/1 | 1 | **0%** |
| A09 - Logging Failures | 1/1 | 1 | **100%** |
| A10 - Server-Side Request Forgery | 2/2 | 2 | **100%** |

### ML Top 10 (7 vulnerabilities)
- **Detected**: 4/7
- **Detection Rate**: **57%**
- **Missed**: 3

| Category | Status |
|----------|--------|
| ML-001 - Model Info Leakage | ✅ Detected |
| ML-002 - Unthrottled Predictions | ❌ Missed |
| ML-003 - Arbitrary Model Loading | ✅ Detected |
| ML-004 - Raw PII in Training Data | ✅ Detected |
| ML-005 - ScriptEngine Code Execution | ✅ Detected |
| ML-006 - Unauthorized Model Export | ❌ Missed |
| ML-007 - Debug PII Exposure | ❌ Missed |

### Agentic AI Top 10 (5 vulnerabilities)
- **Detected**: 3/5
- **Detection Rate**: **60%**
- **Missed**: 2

| Category | Status |
|----------|--------|
| AG-001 - Secrets to Third-Party | ✅ Detected |
| AG-002 - AI Command Execution | ✅ Detected |
| AG-003 - Excessive AI Permissions | ❌ Missed |
| AG-004 - Dynamic Plugin Import | ✅ Detected |
| AG-005 - Auto-Approval Financial Actions | ❌ Missed |

## Detection Rate by Complexity

| Complexity Level | Detection Rate | Description |
|------------------|----------------|-------------|
| **Traditional Web Security** | **92%** (22/24) | Classic injection, access control, crypto |
| **Configuration Issues** | **75%** (3/4) | CORS, actuator exposure, error handling |
| **Business Logic Flaws** | **25%** (1/4) | Financial workflows, approval bypasses |
| **ML/AI Security** | **58%** (7/12) | ML-specific and AI-specific vulnerabilities |

## Scanner Output Statistics
- **Total Scanner Findings**: 113
- **True Positives** (matched planted vulns): 27
- **Additional Findings**: 86
- **Potential False Positive Rate**: 76%

## Severity Distribution of Detected Vulnerabilities

| Severity | Count | Percentage |
|----------|-------|------------|
| Critical | 7 | 26% |
| High | 57 | 50% |
| Medium | 31 | 27% |
| Low | 4 | 4% |

## Key Performance Metrics

### Excellent Performance (90-100% detection)
- **Access Control Issues**: 100%
- **Injection Vulnerabilities**: 100% 
- **Cryptographic Failures**: 100%
- **Authentication Issues**: 100%

### Poor Performance (0-50% detection)
- **Vulnerable Components**: 0%
- **Software Integrity**: 0%
- **Insecure Design**: 33%

### Mixed Performance (51-89% detection)
- **ML Security**: 57%
- **AI Security**: 60%
- **Configuration Issues**: 75%

## Summary
The scanner excels at **traditional web application security** with near-perfect detection of injection, access control, and cryptographic issues. However, it struggles with **business logic flaws** (25% detection) and shows **moderate capability** in emerging **ML/AI security** domains (58% combined detection rate).