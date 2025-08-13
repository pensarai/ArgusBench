# Scanner Performance Statistics Report: oss20b

## Overall Detection Rate
- **Total Planted Vulnerabilities**: 42
- **Successfully Detected**: 22
- **Detection Rate**: **52%**
- **Missed Vulnerabilities**: 20 (48%)

## Detection by Vulnerability Category

### OWASP Top 10 (26 vulnerabilities)
- **Detected**: 11/26
- **Detection Rate**: **42%**
- **Missed**: 15

| Category | Detected | Total | Rate |
|----------|----------|-------|------|
| A01 - Broken Access Control | 3/4 | 4 | **75%** |
| A02 - Cryptographic Failures | 1/3 | 3 | **33%** |
| A03 - Injection | 4/5 | 5 | **80%** |
| A04 - Insecure Design | 0/2 | 2 | **0%** |
| A05 - Security Misconfiguration | 0/3 | 3 | **0%** |
| A06 - Vulnerable Components | 1/3 | 3 | **33%** |
| A07 - Authentication Failures | 1/3 | 3 | **33%** |
| A08 - Software Integrity Failures | 1/1 | 1 | **100%** |
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
| WEB-AG-004 - Excessive AI Permissions | ✅ Detected |
| WEB-AG-005 - AI Training Data Exposure | ❌ Missed |
| WEB-AG-006 - Insecure AI Plugin Loader | ✅ Detected |
| WEB-AG-007 - AI Response as Commands | ✅ Detected |
| WEB-AG-008 - AI Supply Chain Vulnerability | ✅ Detected |

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
| **Traditional Web Security** | **58%** (11/19) | Classic injection, access control, crypto |
| **AI Security** | **75%** (6/8) | AI-specific vulnerabilities and attacks |
| **Configuration Issues** | **0%** (0/3) | CORS, environment secrets, directory listing |
| **ML Security** | **0%** (0/5) | Machine learning specific vulnerabilities |
| **Advanced Attacks** | **43%** (3/7) | Complex injection, deserialization, SSRF |

## Scanner Output Statistics
- **Total Scanner Findings**: 61
- **True Positives** (matched planted vulns): 22
- **Additional Findings**: 39
- **Potential False Positive Rate**: 64%

## Severity Distribution of Detected Vulnerabilities

| Severity | Count | Percentage |
|----------|-------|------------|
| Critical | 6 | 10% |
| High | 46 | 75% |
| Medium | 9 | 15% |
| Low | 0 | 0% |

## True Positive Detections

### High-Confidence Matches (20 findings)
1. **Hardcoded OpenAI Key** - api.ts:3
2. **XSS in Comments** - TaskComments.tsx:8
3. **Direct Prompt Injection** - AIAssistant.tsx:13
4. **Excessive AI Permissions** - ai-config.ts:1
5. **Hardcoded JWT Secret** - jwt.ts:9
6. **Hardcoded Refresh Secret** - jwt.ts:13
7. **Missing JWT Verification** - jwt.ts:32
8. **Insecure AI Plugin Loader** - ai-plugins.ts:5
9. **AI Supply Chain Vulnerability** - ai-integration.ts:4
10. **IDOR in Task Access** - taskService.ts:62
11. **NoSQL-like Injection** - searchService.ts:5
12. **Command Injection in File Processing** - fileService.ts:55
13. **Username Enumeration** - authService.ts:54
14. **Profile Access IDOR** - users.ts:17
15. **Admin Privilege Escalation** - users.ts:19
16. **URL Fetch SSRF** - webhooks.ts:20
17. **Insecure Deserialization** - webhooks.ts:25
18. **AI Response as Commands** - ai.ts:37
19. **AI Code Execution** - ai.ts:22
20. **SQL Injection in Task Search** - Task.ts:149
21. **Sensitive Data in Logs** - authController.ts:45
22. **Weak Authentication Storage** - database connections

### Medium-Confidence Matches (2 findings)
- Database credential issues matched to authentication vulnerabilities
- Related hardcoded credential detections

## Critical Missed Vulnerabilities
1. **WEB-A01-004**: File Download Path Traversal
2. **WEB-A02-003**: Plaintext Sensitive Storage - PII unencrypted
3. **WEB-A03-005**: Template Injection in Email
4. **WEB-A04-001**: No Rate Limiting on Password Reset
5. **WEB-A04-002**: Insecure Password Reset Flow
6. **WEB-A05-001**: Exposed Environment Secrets in .env
7. **WEB-A05-002**: Missing Security Headers / Permissive CORS
8. **WEB-A05-003**: Directory Listing Enabled
9. **WEB-A06-001**: Outdated React Version
10. **WEB-A06-002**: Vulnerable NPM Packages
11. **WEB-A07-001**: Weak Password Policy
12. **WEB-A07-002**: Non-expiring Sessions
13. **WEB-A10-002**: Webhook SSRF
14. **WEB-AG-003**: AI Data Leakage
15. **WEB-AG-005**: AI Training Data Exposure
16. **All ML Vulnerabilities** (WEB-ML-001 through WEB-ML-005)

## Comparison with Other Scanners

| Metric | oss20b | gpt-oss-120b | o3mini | Performance |
|--------|--------|--------------|--------|-------------|
| **Overall Detection Rate** | 52% | 57% | 57% | 3rd |
| **OWASP Detection** | 42% | 58% | 54% | **3rd** |
| **AI Detection** | 75% | 75% | 38% | **Tied 1st** |
| **ML Detection** | 0% | 20% | 0% | **3rd** |
| **Total Findings** | 61 | 45 | 65 | **2nd** |
| **False Positive Rate** | 64% | 47% | 63% | **3rd** (highest) |
| **Precision** | 36% | 53% | 37% | **3rd** (lowest) |

## Performance Metrics by Category

### Excellent Performance (90-100% detection)
- **Software Integrity Failures**: 100%
- **Logging Failures**: 100%

### Good Performance (70-89% detection)
- **Injection Vulnerabilities**: 80%
- **Access Control Issues**: 75%
- **Agentic AI Security**: 75%

### Average Performance (50-69% detection)
- **SSRF Vulnerabilities**: 50%

### Poor Performance (0-49% detection)
- **Cryptographic Failures**: 33%
- **Vulnerable Components**: 33%
- **Authentication Issues**: 33%
- **OWASP Overall**: 42%
- **Insecure Design**: 0%
- **Security Misconfiguration**: 0%
- **ML Security**: 0%

## False Positive Analysis
- **Total False Positives**: 39 (64% of findings)
- **Over-Detection in Models/Database**: Multiple SQL injection warnings across database query functions
- **Controller Input Validation Overreach**: Excessive unvalidated input warnings
- **File Handling Over-Sensitivity**: Multiple file upload and handling concerns
- **Logging Security Over-Flagging**: Broad sensitive data logging warnings
- **Authorization Check Duplication**: Repeated IDOR and access control warnings