# Scanner Performance Statistics Report: gpt-oss-120b

## Overall Detection Rate
- **Total Planted Vulnerabilities**: 42
- **Successfully Detected**: 24
- **Detection Rate**: **57%**
- **Missed Vulnerabilities**: 18 (43%)

## Detection by Vulnerability Category

### OWASP Top 10 (26 vulnerabilities)
- **Detected**: 15/26
- **Detection Rate**: **58%**
- **Missed**: 11

| Category | Detected | Total | Rate |
|----------|----------|-------|------|
| A01 - Broken Access Control | 3/4 | 4 | **75%** |
| A02 - Cryptographic Failures | 2/3 | 3 | **67%** |
| A03 - Injection | 2/5 | 5 | **40%** |
| A04 - Insecure Design | 1/2 | 2 | **50%** |
| A05 - Security Misconfiguration | 0/3 | 3 | **0%** |
| A06 - Vulnerable Components | 1/3 | 3 | **33%** |
| A07 - Authentication Failures | 1/3 | 3 | **33%** |
| A08 - Software Integrity Failures | 0/1 | 1 | **0%** |
| A09 - Logging Failures | 1/1 | 1 | **100%** |
| A10 - Server-Side Request Forgery | 1/2 | 2 | **50%** |

### Agentic AI Top 10 (8 vulnerabilities)
- **Detected**: 6/8
- **Detection Rate**: **75%**
- **Missed**: 2

| Category | Status |
|----------|--------|
| WEB-AG-001 - Direct Prompt Injection | ✅ Detected |
| WEB-AG-002 - AI Code Execution | ✅ Detected |
| WEB-AG-003 - AI Data Leakage | ❌ Missed |
| WEB-AG-004 - Excessive AI Permissions | ❌ Missed |
| WEB-AG-005 - AI Training Data Exposure | ❌ Missed |
| WEB-AG-006 - Insecure AI Plugin Loader | ✅ Detected |
| WEB-AG-007 - AI Response as Commands | ✅ Detected |
| WEB-AG-008 - AI Supply Chain Vulnerability | ✅ Detected |

### ML Top 10 (5 vulnerabilities)
- **Detected**: 1/5
- **Detection Rate**: **20%**
- **Missed**: 4

| Category | Status |
|----------|--------|
| WEB-ML-001 - ML Input Manipulation | ✅ Detected |
| WEB-ML-002 - ML Model Information Leakage | ❌ Missed |
| WEB-ML-003 - ML API No Rate Limiting | ❌ Missed |
| WEB-ML-004 - ML Supply Chain Attack | ❌ Missed |
| WEB-ML-005 - ML Model Theft | ❌ Missed |

## Detection Rate by Complexity

| Complexity Level | Detection Rate | Description |
|------------------|----------------|-------------|
| **Traditional Web Security** | **65%** (13/20) | Classic injection, access control, crypto |
| **AI Security** | **75%** (6/8) | AI-specific vulnerabilities and attacks |
| **Configuration Issues** | **0%** (0/3) | CORS, environment secrets, directory listing |
| **ML Security** | **20%** (1/5) | Machine learning specific vulnerabilities |
| **Advanced Attacks** | **43%** (3/7) | Complex injection, deserialization, SSRF |

## Scanner Output Statistics
- **Total Scanner Findings**: 45
- **True Positives** (matched planted vulns): 24
- **Additional Findings**: 21
- **Potential False Positive Rate**: 47%

## Severity Distribution of Detected Vulnerabilities

| Severity | Count | Percentage |
|----------|-------|------------|
| Critical | 4 | 9% |
| High | 30 | 67% |
| Medium | 10 | 22% |
| Low | 1 | 2% |

## True Positive Detections

### High-Confidence Matches (22 findings)
1. **Direct Prompt Injection** - AIAssistant.tsx:14
2. **XSS in Comments** - TaskComments.tsx:8
3. **Hardcoded Refresh Secret** - jwt.ts:13
4. **Insecure Token Verification** - jwt.ts:32
5. **Insecure JWT Decode** - jwt.ts:46
6. **JWT Secret Hardcoded** - jwt.ts:9
7. **Insecure AI Plugin Loader** - ai-plugins.ts:3
8. **AI Supply Chain Vulnerability** - ai-integration.ts:1
9. **URL Fetch SSRF** - ai-integration.ts:1
10. **NoSQL-like Injection** - searchService.ts:5
11. **Weak Password Policy** - authService.ts:29
12. **Username Enumeration** - authService.ts:53
13. **ML Input Manipulation** - ml.ts:8
14. **AI Code Execution** - ai.ts:22
15. **AI Response as Commands** - ai.ts:37
16. **File Download Path Traversal** - files.ts:31
17. **Insecure Password Reset Flow** - authController.ts:108
18. **Sensitive Data in Logs** - authController.ts:44
19. **Admin Privilege Escalation** - users.ts:19
20. **Profile Access IDOR** - users.ts:17
21. **Weak Password Hashing (MD5)** - crypto.ts:6,10
22. **CORS Misconfiguration** - app.ts:23

### Medium-Confidence Matches (2 findings)
- Database credential issues matched to authentication vulnerabilities
- Some IDOR instances matched across different files/components

## Critical Missed Vulnerabilities
1. **WEB-A03-001**: SQL injection in task search - critical traditional vulnerability
2. **WEB-A03-003**: Command injection in file processing
3. **WEB-A02-001**: Hardcoded API keys in frontend
4. **WEB-A05-001**: Exposed environment secrets in .env file
5. **WEB-A05-002**: Missing security headers / permissive CORS
6. **WEB-A06-001**: Outdated React version with known CVEs
7. **WEB-A08-001**: Insecure deserialization (eval)
8. **WEB-ML-002**: ML Model Information Leakage
9. **WEB-ML-003**: ML API No Rate Limiting
10. **WEB-ML-004**: ML Supply Chain Attack
11. **WEB-ML-005**: ML Model Theft

## Comparison with Other Scanners

| Metric | gpt-oss-120b | o3mini | oss20b | Performance |
|--------|--------------|--------|--------|-------------|
| **Overall Detection Rate** | 57% | 57% | 52% | Tied 1st |
| **OWASP Detection** | 58% | 54% | 42% | **1st** |
| **AI Detection** | 75% | 38% | 75% | **Tied 1st** |
| **ML Detection** | 20% | 0% | 0% | **1st** |
| **Total Findings** | 45 | 65 | 61 | **1st** (most efficient) |
| **False Positive Rate** | 47% | 63% | 64% | **1st** (lowest) |
| **Precision** | 53% | 37% | 36% | **1st** (highest) |

## Performance Metrics by Category

### Excellent Performance (90-100% detection)
- **Logging Failures**: 100%

### Good Performance (70-89% detection)
- **Access Control Issues**: 75%
- **Agentic AI Security**: 75%

### Average Performance (50-69% detection)
- **Cryptographic Failures**: 67%
- **OWASP Overall**: 58%
- **SSRF Vulnerabilities**: 50%
- **Insecure Design**: 50%

### Poor Performance (0-49% detection)
- **Injection Vulnerabilities**: 40%
- **Authentication Issues**: 33%
- **Vulnerable Components**: 33%
- **ML Security**: 20%
- **Security Misconfiguration**: 0%
- **Software Integrity**: 0%

## False Positive Analysis
- **Total False Positives**: 21 (47% of findings)
- **Controller Over-Detection**: Multiple IDOR warnings in different controllers
- **Logging Security Concerns**: Over-flagging of standard logging practices
- **Authentication Flow Issues**: Excessive JWT role claim warnings
- **File Handling Security**: MIME type and upload concerns