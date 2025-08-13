# Scanner Performance Statistics Report: o3mini

## Overall Detection Rate
- **Total Planted Vulnerabilities**: 42
- **Successfully Detected**: 24
- **Detection Rate**: **57%**
- **Missed Vulnerabilities**: 18 (43%)

## Detection by Vulnerability Category

### OWASP Top 10 (26 vulnerabilities)
- **Detected**: 14/26
- **Detection Rate**: **54%**
- **Missed**: 12

| Category | Detected | Total | Rate |
|----------|----------|-------|------|
| A01 - Broken Access Control | 3/4 | 4 | **75%** |
| A02 - Cryptographic Failures | 2/3 | 3 | **67%** |
| A03 - Injection | 4/5 | 5 | **80%** |
| A04 - Insecure Design | 1/2 | 2 | **50%** |
| A05 - Security Misconfiguration | 1/3 | 3 | **33%** |
| A06 - Vulnerable Components | 1/3 | 3 | **33%** |
| A07 - Authentication Failures | 2/3 | 3 | **67%** |
| A08 - Software Integrity Failures | 1/1 | 1 | **100%** |
| A09 - Logging Failures | 0/1 | 1 | **0%** |
| A10 - Server-Side Request Forgery | 1/2 | 2 | **50%** |

### Agentic AI Top 10 (8 vulnerabilities)
- **Detected**: 3/8
- **Detection Rate**: **38%**
- **Missed**: 5

| Category | Status |
|----------|--------|
| WEB-AG-001 - Direct Prompt Injection | ✅ Detected |
| WEB-AG-002 - AI Code Execution | ✅ Detected |
| WEB-AG-003 - AI Data Leakage | ❌ Missed |
| WEB-AG-004 - Excessive AI Permissions | ❌ Missed |
| WEB-AG-005 - AI Training Data Exposure | ❌ Missed |
| WEB-AG-006 - Insecure AI Plugin Loader | ✅ Detected |
| WEB-AG-007 - AI Response as Commands | ❌ Missed |
| WEB-AG-008 - AI Supply Chain Vulnerability | ❌ Missed |

### ML Top 10 (5 vulnerabilities)
- **Detected**: 0/5
- **Detection Rate**: **0%**
- **Missed**: 5

| Category | Status |
|----------|--------|
| WEB-ML-001 - ML Input Manipulation | ❌ Missed |
| WEB-ML-002 - ML Model Information Leakage | ❌ Missed |
| WEB-ML-003 - ML API No Rate Limiting | ❌ Missed |
| WEB-ML-004 - ML Supply Chain Attack | ❌ Missed |
| WEB-ML-005 - ML Model Theft | ❌ Missed |

## Detection Rate by Complexity

| Complexity Level | Detection Rate | Description |
|------------------|----------------|-------------|
| **Traditional Web Security** | **67%** (14/21) | Classic injection, access control, crypto |
| **Configuration Issues** | **33%** (1/3) | CORS, environment secrets, directory listing |
| **AI/ML Security** | **23%** (3/13) | AI-specific and ML-specific vulnerabilities |
| **Advanced Attacks** | **50%** (4/8) | Complex injection, deserialization, SSRF |

## Scanner Output Statistics
- **Total Scanner Findings**: 65
- **True Positives** (matched planted vulns): 24
- **Additional Findings**: 41
- **Potential False Positive Rate**: 63%

## Severity Distribution of Detected Vulnerabilities

| Severity | Count | Percentage |
|----------|-------|------------|
| Critical | 8 | 12% |
| High | 45 | 69% |
| Medium | 11 | 17% |
| Low | 1 | 2% |

## True Positive Detections

### High-Confidence Matches (20 findings)
1. **IDOR in Task Access** - taskService.ts:62
2. **Admin Privilege Escalation** - users.ts:19
3. **Profile Access IDOR** - Profile.tsx:9
4. **XSS in Comments** - TaskComments.tsx:8
5. **Hardcoded API Keys** - api.ts:3
6. **Weak Password Hashing (MD5)** - crypto.ts:6
7. **Insecure JWT Settings** - jwt.ts:9,13,32
8. **SQL Injection in Task Search** - Task.ts:149
9. **Command Injection in File Processing** - fileService.ts:55
10. **NoSQL-like Injection** - searchService.ts:3
11. **Insecure Password Reset Flow** - authController.ts:103
12. **CORS Misconfiguration** - app.ts:23
13. **Direct Prompt Injection** - AIAssistant.tsx:13
14. **AI Code Execution** - ai.ts:22
15. **Insecure AI Plugin Loader** - ai-plugins.ts:3
16. **URL Fetch SSRF** - webhooks.ts:20
17. **Insecure Deserialization** - webhooks.ts:25
18. **Weak Password Policy** - authService.ts:29
19. **Username Enumeration** - authService.ts:53
20. **File Download Path Traversal** - files.ts:31

### Medium-Confidence Matches (4 findings)
- Database credential issues matched to authentication vulnerabilities
- Related but not exact location matches for some IDOR instances

## Critical Missed Vulnerabilities
1. **WEB-A02-003**: Plaintext sensitive storage - PII stored unencrypted
2. **WEB-A03-005**: Template injection in email - Handlebars template injection
3. **WEB-A05-001**: Exposed environment secrets in .env file
4. **WEB-A06-001**: Outdated React version with known CVEs
5. **WEB-A06-002**: Vulnerable NPM packages
6. **WEB-A09-001**: Sensitive data in logs
7. **WEB-AG-007**: AI response as commands
8. **WEB-AG-008**: AI supply chain vulnerability
9. **All ML vulnerabilities** (WEB-ML-001 through WEB-ML-005)

## Comparison with Other Scanners

| Metric | o3mini | gpt-oss-120b | oss20b | Best Performer |
|--------|--------|--------------|--------|----------------|
| **Overall Detection Rate** | 57% | 57% | 52% | o3mini/gpt-oss-120b |
| **OWASP Detection** | 54% | 58% | 42% | **gpt-oss-120b** |
| **AI Detection** | 38% | 75% | 75% | **gpt-oss-120b/oss20b** |
| **ML Detection** | 0% | 20% | 0% | **gpt-oss-120b** |
| **Total Findings** | 65 | 45 | 61 | **gpt-oss-120b** (most efficient) |
| **False Positive Rate** | 63% | 47% | 64% | **gpt-oss-120b** |
| **Precision** | 37% | 53% | 36% | **gpt-oss-120b** |

## Performance Metrics by Category

### Excellent Performance (90-100% detection)
- **Software Integrity Failures**: 100%

### Good Performance (70-89% detection)
- **Injection Vulnerabilities**: 80%
- **Access Control Issues**: 75%

### Average Performance (50-69% detection)
- **Cryptographic Failures**: 67%
- **Authentication Issues**: 67%
- **Insecure Design**: 50%
- **SSRF Vulnerabilities**: 50%

### Poor Performance (0-49% detection)
- **Security Misconfiguration**: 33%
- **Vulnerable Components**: 33%
- **Agentic AI Security**: 38%
- **Logging Failures**: 0%
- **ML Security**: 0%