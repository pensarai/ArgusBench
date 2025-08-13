# Scanner Performance Statistics Report: b2_gpt-oss-120b

## Overall Detection Rate
- **Total Planted Vulnerabilities**: 37
- **Successfully Detected**: 24
- **Detection Rate**: **65%**
- **Missed Vulnerabilities**: 13 (35%)

## Detection by Vulnerability Category

### OWASP Top 10 (25 vulnerabilities)
- **Detected**: 18/25
- **Detection Rate**: **72%**
- **Missed**: 7

| Category | Detected | Total | Rate |
|----------|----------|-------|------|
| A01 - Broken Access Control | 4/5 | 5 | **80%** |
| A02 - Cryptographic Failures | 4/4 | 4 | **100%** |
| A03 - Injection | 4/5 | 5 | **80%** |
| A04 - Insecure Design | 0/3 | 3 | **0%** |
| A05 - Security Misconfiguration | 2/3 | 3 | **67%** |
| A06 - Vulnerable Components | 0/1 | 1 | **0%** |
| A07 - Authentication Failures | 2/2 | 2 | **100%** |
| A08 - Software Integrity Failures | 0/1 | 1 | **0%** |
| A09 - Logging Failures | 1/1 | 1 | **100%** |
| A10 - Server-Side Request Forgery | 2/2 | 2 | **100%** |

### ML Top 10 (7 vulnerabilities)
- **Detected**: 2/7
- **Detection Rate**: **29%**
- **Missed**: 5

| Category | Status |
|----------|--------|
| ML-001 - Model Info Leakage | ❌ Missed |
| ML-002 - Unthrottled Predictions | ❌ Missed |
| ML-003 - Arbitrary Model Loading | ❌ Missed |
| ML-004 - Raw PII in Training Data | ❌ Missed |
| ML-005 - ScriptEngine Code Execution | ✅ Detected |
| ML-006 - Unauthorized Model Export | ❌ Missed |
| ML-007 - Debug PII Exposure | ✅ Detected |

### Agentic AI Top 10 (5 vulnerabilities)
- **Detected**: 4/5
- **Detection Rate**: **80%**
- **Missed**: 1

| Category | Status |
|----------|--------|
| AG-001 - Secrets to Third-Party | ✅ Detected |
| AG-002 - AI Command Execution | ✅ Detected |
| AG-003 - Excessive AI Permissions | ❌ Missed |
| AG-004 - Dynamic Plugin Import | ✅ Detected |
| AG-005 - Auto-Approval Financial Actions | ✅ Detected |

## Detection Rate by Complexity

| Complexity Level | Detection Rate | Description |
|------------------|----------------|-------------|
| **Traditional Web Security** | **85%** (17/20) | Classic injection, access control, crypto |
| **Configuration Issues** | **50%** (2/4) | CORS, actuator exposure, error handling |
| **Business Logic Flaws** | **25%** (1/4) | Financial workflows, approval bypasses |
| **ML/AI Security** | **50%** (6/12) | ML-specific and AI-specific vulnerabilities |

## Scanner Output Statistics
- **Total Scanner Findings**: 87
- **True Positives** (matched planted vulns): 24
- **Additional Findings**: 63
- **Potential False Positive Rate**: 72%

## Severity Distribution of Detected Vulnerabilities

| Severity | Count | Percentage |
|----------|-------|------------|
| Critical | 8 | 9% |
| High | 47 | 54% |
| Medium | 29 | 33% |
| Low | 3 | 3% |

## Key Performance Metrics

### Excellent Performance (90-100% detection)
- **Cryptographic Failures**: 100%
- **Authentication Issues**: 100%
- **SSRF Vulnerabilities**: 100%
- **Logging Failures**: 100%

### Good Performance (70-89% detection)
- **Access Control Issues**: 80%
- **Injection Vulnerabilities**: 80%
- **Agentic AI Security**: 80%

### Poor Performance (0-50% detection)
- **Insecure Design**: 0%
- **Vulnerable Components**: 0%
- **Software Integrity**: 0%
- **ML Security**: 29%

## Comparison with b2_o3-mini Scanner

| Metric | b2_gpt-oss-120b | b2_o3-mini | Difference |
|--------|-----------------|------------|------------|
| **Overall Detection Rate** | 65% | 73% | -8% |
| **OWASP Detection** | 72% | 88% | -16% |
| **ML Detection** | 29% | 57% | -28% |
| **AI Detection** | 80% | 60% | +20% |
| **Total Findings** | 87 | 113 | -26 |
| **False Positive Rate** | 72% | 76% | -4% |

## Strengths vs b2_o3-mini
- **Superior AI Security Detection**: 80% vs 60%
- **Better Auto-Approval Detection**: Detected financial transaction bypasses
- **Strong Hardcoded Credential Detection**: Found more credential exposures
- **Effective Command Injection Detection**: Good coverage of execution vulnerabilities

## Weaknesses vs b2_o3-mini
- **Lower Overall Performance**: 8% lower detection rate
- **Weaker ML Security**: 29% vs 57% detection
- **Missing Business Logic**: Still struggles with workflow bypasses
- **Configuration Gaps**: Missed some actuator and debug exposures
- **Dependency Analysis**: No vulnerable component detection

## Critical Missed Vulnerabilities
1. **JAVA-A04-002**: Financial transaction validation bypass
2. **JAVA-A04-003**: Credit limit approval workflow bypass  
3. **JAVA-A06-001**: Vulnerable Spring Boot dependencies
4. **JAVA-A08-001**: Missing file integrity validation
5. **JAVA-ML-001**: ML model info leakage
6. **JAVA-ML-002**: Unthrottled ML prediction DoS
7. **JAVA-AG-003**: Excessive AI tool permissions

## Recommendations

### **For Security Teams**
1. **Complement with Business Logic Testing**: Manual review still needed for complex workflows
2. **Add Dependency Scanning**: Both scanners miss vulnerable components
3. **ML Security Focused Testing**: Requires specialized ML security assessment
4. **Configuration Auditing**: Manual review of service configurations needed

### **Scanner Selection Insights**
- **b2_gpt-oss-120b** excels at AI-specific vulnerabilities but weaker overall
- **b2_o3-mini** provides better comprehensive coverage 
- Both scanners struggle with business logic and dependency analysis
- Consider using both scanners for complementary coverage

## Summary

The b2_gpt-oss-120b scanner achieved a **65% detection rate**, performing 8% lower than b2_o3-mini overall but showing **superior performance in AI security** (80% vs 60%). It excels at detecting traditional code-level security issues but struggles with **business logic flaws** and **ML-specific vulnerabilities**. The scanner would be most effective when combined with specialized testing for financial workflows and ML security assessments.