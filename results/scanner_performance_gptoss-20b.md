# Scanner Performance Statistics Report: b2_gptoss-20b

## Overall Detection Rate
- **Total Planted Vulnerabilities**: 37
- **Successfully Detected**: 25
- **Detection Rate**: **68%**
- **Missed Vulnerabilities**: 12 (32%)

## Detection by Vulnerability Category

### OWASP Top 10 (25 vulnerabilities)
- **Detected**: 18/25
- **Detection Rate**: **72%**
- **Missed**: 7

| Category | Detected | Total | Rate |
|----------|----------|-------|------|
| A01 - Broken Access Control | 4/5 | 5 | **80%** |
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
- **Detected**: 3/7
- **Detection Rate**: **43%**
- **Missed**: 4

| Category | Status |
|----------|--------|
| ML-001 - Model Info Leakage | ❌ Missed *(Related path exposure detected)* |
| ML-002 - Unthrottled Predictions | ❌ Missed |
| ML-003 - Arbitrary Model Loading | ❌ Missed *(SSRF detected in related context)* |
| ML-004 - Raw PII in Training Data | ✅ Detected *(PII storage identified)* |
| ML-005 - ScriptEngine Code Execution | ✅ Detected *(Multiple script injection findings)* |
| ML-006 - Unauthorized Model Export | ❌ Missed |
| ML-007 - Debug PII Exposure | ✅ Detected *(Debug endpoint exposure)* |

### Agentic AI Top 10 (5 vulnerabilities)
- **Detected**: 4/5
- **Detection Rate**: **80%**
- **Missed**: 1

| Category | Status |
|----------|--------|
| AG-001 - Secrets to Third-Party | ✅ Detected *(Credential exposure findings)* |
| AG-002 - AI Command Execution | ✅ Detected *(Command execution endpoints)* |
| AG-003 - Excessive AI Permissions | ❌ Missed |
| AG-004 - Dynamic Plugin Import | ✅ Detected *(Connector loading vulnerabilities)* |
| AG-005 - Auto-Approval Financial Actions | ✅ Detected *(Auto-approve endpoints)* |

## Detection Rate by Complexity

| Complexity Level | Detection Rate | Description |
|------------------|----------------|-------------|
| **Traditional Web Security** | **89%** (16/18) | Classic injection, access control, crypto |
| **Configuration Issues** | **100%** (4/4) | CORS, actuator exposure, error handling |
| **Business Logic Flaws** | **33%** (1/3) | Financial workflows, approval bypasses |
| **ML/AI Security** | **58%** (7/12) | ML-specific and AI-specific vulnerabilities |

## Scanner Output Statistics
- **Total Scanner Findings**: 100
- **True Positives** (matched planted vulns): 25
- **Additional Findings**: 75
- **Potential False Positive Rate**: 75%

## Severity Distribution of Detected Vulnerabilities

| Severity | Count | Percentage |
|----------|-------|------------|
| Critical | 18 | 18% |
| High | 66 | 66% |
| Medium | 15 | 15% |
| Low | 1 | 1% |

## Key Performance Metrics

### Excellent Performance (90-100% detection)
- **Cryptographic Failures**: 100%
- **Injection Vulnerabilities**: 100%
- **Authentication Issues**: 100%
- **SSRF Vulnerabilities**: 100%
- **Logging Failures**: 100%
- **Security Misconfiguration**: 100%

### Good Performance (70-89% detection)
- **Access Control Issues**: 80%
- **Agentic AI Security**: 80%

### Poor Performance (0-50% detection)
- **Insecure Design**: 33%
- **ML Security**: 43%
- **Vulnerable Components**: 0%
- **Software Integrity**: 0%

## Comparison with Previous Scanners

| Metric | b2_gptoss-20b | b2_o3-mini | b2_gpt-oss-120b | Rank |
|--------|---------------|------------|------------------|------|
| **Overall Detection Rate** | 68% | 73% | 65% | 🥈 2nd |
| **OWASP Detection** | 72% | 88% | 72% | 🥉 3rd (tied) |
| **ML Detection** | 43% | 57% | 29% | 🥈 2nd |
| **AI Detection** | 80% | 60% | 80% | 🥇 1st (tied) |
| **Total Findings** | 100 | 113 | 87 | 🥈 2nd |
| **False Positive Rate** | 75% | 76% | 72% | 🥈 2nd |

## Strengths vs Other Scanners
- **Excellent Injection Detection**: 100% coverage across all injection types
- **Strong AI Security**: Tied for best performance at 80%
- **Comprehensive Error Handling Detection**: Found multiple information disclosure issues
- **Effective Authentication Testing**: Perfect detection of auth bypass issues
- **Good SSRF Coverage**: Detected both webhook and image processing SSRF

## Weaknesses vs Other Scanners
- **Lower Overall Performance**: 68% vs b2_o3-mini's 73%
- **Business Logic Blindness**: Only 33% detection of complex workflow issues
- **No Dependency Analysis**: 0% detection of vulnerable components
- **Missing ML Model Security**: Failed to detect model loading and export issues
- **High False Positive Rate**: 75% of findings don't match planted vulnerabilities

## Critical Missed Vulnerabilities
1. **JAVA-A01-005**: Cross-tenant file download IDOR
2. **JAVA-A04-002**: Financial transaction validation bypass
3. **JAVA-A04-003**: Credit limit approval workflow bypass
4. **JAVA-A06-001**: Vulnerable Spring Boot dependencies
5. **JAVA-A08-001**: Missing file integrity validation
6. **JAVA-ML-001**: ML model information leakage
7. **JAVA-ML-002**: Unthrottled ML prediction DoS
8. **JAVA-ML-003**: Arbitrary model loading from URLs
9. **JAVA-ML-006**: Unauthorized model export
10. **JAVA-AG-003**: Excessive AI tool permissions

## Notable Strengths
- **Deep Injection Analysis**: Detected LDAP, SQL, command, and template injection with high fidelity
- **Comprehensive Debug Endpoint Detection**: Found multiple debug information disclosure issues
- **Strong Script Execution Detection**: Multiple findings for ScriptEngine vulnerabilities
- **Effective CORS Analysis**: Properly identified permissive CORS configuration
- **Good Logging Security Analysis**: Detected credential logging across multiple contexts

## Recommendations

### **For Security Teams**
1. **Complement with Business Logic Testing**: Manual review essential for complex financial workflows
2. **Add Dependency Scanning**: Both scanners miss vulnerable components (0% on A06)
3. **ML Security Focused Testing**: Requires specialized assessment for model-specific vulnerabilities
4. **Configuration Auditing**: While strong on CORS/debug, needs broader config review
5. **False Positive Management**: 75% noise ratio requires careful triage

### **Scanner Selection Insights**
- **b2_gptoss-20b** provides middle-tier performance with excellent injection detection
- **Strong for traditional web vulnerabilities** but weaker on complex business logic
- **Good AI/ML coverage** but misses some model-specific issues
- **High finding volume** with significant false positive management needed

## Summary

The b2_gptoss-20b scanner achieved a **68% detection rate**, ranking second among the three scanners tested. It demonstrates **exceptional performance in injection detection** (100%) and **strong AI security coverage** (80% tied for first). The scanner excels at identifying traditional code-level security issues and provides comprehensive coverage of authentication, cryptographic, and configuration vulnerabilities.

However, it struggles with **business logic flaws** (33% detection) and **dependency analysis** (0%). The scanner generates a high volume of findings (100 total) but has a **75% false positive rate**, requiring careful triage.

**Best use case**: Organizations seeking comprehensive traditional web application security scanning with strong injection detection capabilities, especially those with AI/ML components requiring security assessment.

**Should be combined with**: Business logic testing, dependency scanning tools, and manual security review for complete coverage.