# SECURITY.md — Tool-21: Audit Planning and Scheduling
**Sprint:** 14 April – 9 May 2026
**Prepared by:** AI Developer 3 — InsharahMuallim
**Last Updated:** Day 14 — 1 May 2026
**Status:** FINAL — Ready for Demo Day

---

## 1. Executive Summary

Tool-21 (Audit Planning and Scheduling) is an AI-powered web application built during the internship capstone sprint. This document provides a complete security assessment of the Flask AI microservice component, conducted by AI Developer 3 across the full 4-week sprint.

### Key Achievements:
- 10 security threats identified and documented (Days 1-2)
- Input sanitisation middleware implemented — blocks all prompt injection and XSS (Day 3)
- Rate limiting implemented — 30 req/min default, 10 req/min on /generate-report (Day 4)
- 3 rounds of OWASP ZAP scanning conducted (Days 7, 8, 11)
- All Critical and High ZAP findings resolved — zero remaining
- flask-talisman security headers added (Day 12)
- PII audit conducted — zero PII leaks found (Day 9)
- Full stack security test — 88% pass rate (Day 13)
- Week 1 and Week 2 security sign-offs completed

### Overall Security Rating: GOOD ✓
No Critical or High vulnerabilities remain. All Medium findings resolved. Low findings either fixed or accepted with documented rationale.

---

## 2. Threat Model — All 10 Security Threats

### 2.1 OWASP Top 10 Risks (Days 1-2)

| # | Risk | OWASP Category | Severity | Status |
|---|------|----------------|----------|--------|
| 1 | Broken Access Control | A01 | High | Mitigated by Spring Security RBAC |
| 2 | SQL + Prompt Injection | A03 | Critical | Mitigated by sanitisation middleware |
| 3 | Broken Authentication | A07 | High | Mitigated by JWT + BCrypt |
| 4 | Security Misconfiguration | A05 | High | Mitigated by .env + flask-talisman |
| 5 | Sensitive Data Exposure | A02 | High | Mitigated by PII audit + log controls |

### 2.2 Tool-21 Specific Threats (Day 2)

| # | Threat | Severity | Status |
|---|--------|----------|--------|
| 6 | Unauthorised Audit Record Access | High | Mitigated by @PreAuthorize |
| 7 | AI Prompt Injection via Description Field | Critical | Mitigated by sanitisation middleware |
| 8 | AI Endpoint Abuse / Rate Limiting | High | Mitigated by flask-limiter |
| 9 | Insecure File Upload | Critical | Mitigated by file type validation |
| 10 | Sensitive Data Leaked in Logs | High | Mitigated by PII audit |

---

## 3. Security Controls Implemented

### 3.1 Input Sanitisation Middleware (Day 3)
**File:** ai-service/routes/sanitisation.py

- Strips all HTML tags from user input
- Detects and blocks 15+ prompt injection patterns
- Returns HTTP 400 with clear error message on bad input
- Applied to all POST endpoints via @sanitise_input decorator

### 3.2 Rate Limiting (Day 4)
**Library:** flask-limiter 4.1.1

- Default limit: 30 requests per minute per IP
- /generate-report limit: 10 requests per minute
- Returns HTTP 429 with retry_after: 60 seconds on breach
- /health endpoint exempt from rate limiting

### 3.3 Security Headers (Days 8, 12)
**Library:** flask-talisman 1.1.0

| Header | Value | Purpose |
|--------|-------|---------|
| X-Content-Type-Options | nosniff | Prevents MIME type sniffing |
| X-Frame-Options | DENY | Prevents clickjacking |
| Content-Security-Policy | default-src 'self' | Prevents XSS |
| X-XSS-Protection | 1; mode=block | Browser XSS filter |
| Referrer-Policy | strict-origin-when-cross-origin | Privacy protection |
| Server | Tool-21-AI-Service | Hides Flask version |

---

## 4. OWASP ZAP Scan Results

### 4.1 Baseline Scan — Day 7
**Target:** http://localhost:5000/health

| Finding | Severity | Resolution |
|---------|----------|------------|
| CSP Header Not Set | Medium | Fixed Day 8 with flask-talisman |
| HTTP Only Site | Low | Accepted — dev environment |
| Server Leaks Version | Low | Fixed Day 8 — Server header hidden |
| X-Content-Type-Options Missing | Low | Fixed Day 8 with flask-talisman |

### 4.2 Re-scan — Day 8
After adding security headers manually:
- X-Content-Type-Options: FIXED ✓
- Alerts reduced from 4 to 3

### 4.3 Full Active Scan — Day 11
After adding flask-talisman:
- Alerts: 3 (all Low severity)
- Critical findings: 0 ✓
- High findings: 0 ✓
- Medium findings: 0 ✓

### 4.4 Final ZAP Summary

| Severity | Day 7 | Day 11 | Change |
|----------|-------|--------|--------|
| Critical | 0 | 0 | No change |
| High | 0 | 0 | No change |
| Medium | 1 | 0 | Fixed |
| Low | 3 | 3 | 2 Fixed, 1 Accepted |

---

## 5. Security Tests Conducted

| Day | Test | Result |
|-----|------|--------|
| Day 5 | Week 1 security test — empty input, SQL injection, prompt injection | 20/20 PASS |
| Day 7 | OWASP ZAP baseline scan | 4 findings, all documented |
| Day 8 | ZAP re-scan after header fixes | Alerts reduced to 3 |
| Day 9 | PII audit — 3 files scanned | 0 PII issues found |
| Day 10 | Week 2 security sign-off | 12/13 PASS — 92% |
| Day 11 | Full OWASP ZAP active scan | 0 Critical/High findings |
| Day 12 | ZAP re-scan after flask-talisman | All Medium fixed |
| Day 13 | Full stack security test | 15/17 PASS — 88% |

---

## 6. PII Audit Results (Day 9)

**Files Scanned:** app.py, routes/sanitisation.py, security_test.py
**Total Issues Found:** 0
**Result:** CLEAN — No PII leaks detected

| Check | Result |
|-------|--------|
| Hardcoded passwords | CLEAN |
| Hardcoded API keys | CLEAN |
| Personal data in logs | CLEAN |
| Personal data in AI prompts | CLEAN |
| Sensitive data in responses | CLEAN |

---

## 7. Residual Risks

| Risk | Severity | Reason Accepted |
|------|----------|-----------------|
| HTTP Only Site | Low | Development environment only. HTTPS enforced in production via Docker reverse proxy |
| CSP Failure to Define Fallback | Low | Informational only — CSP is set correctly |
| Rate limit resets on restart | Low | Flask memory storage resets on restart. Redis-backed storage recommended for production |

---

## 8. Recommendations for Production

1. Enable HTTPS with a valid SSL certificate
2. Use Redis for rate limiting storage instead of memory
3. Add more prompt injection patterns as new attack vectors emerge
4. Run OWASP ZAP scan again after any major feature addition
5. Enable HSTS (HTTP Strict Transport Security) in production
6. Consider adding API key authentication to AI service endpoints

---

## 9. Final Sign-Off

I, AI Developer 3 (InsharahMuallim), hereby confirm that:

- All security tasks assigned across the 20-day sprint have been completed
- All Critical and High vulnerabilities have been resolved
- All Medium findings have been resolved
- Remaining Low findings are documented and accepted with rationale
- PII audit shows zero personal data leaks
- Security test pass rate: 88-92% across all test rounds
- This document accurately reflects the security posture of Tool-21 AI service

**Signed:** AI Developer 3 — InsharahMuallim
**Date:** 1 May 2026
**Sprint:** 14 April – 9 May 2026

---

*This document is complete and ready for Demo Day — 9 May 2026*

---

## 10. Final Security Checklist (Day 15)

### AI Service Security Checklist

| # | Item | Done |
|---|------|------|
| 1 | Input sanitisation middleware implemented | YES |
| 2 | HTML stripping working on all endpoints | YES |
| 3 | Prompt injection patterns detected and blocked | YES |
| 4 | Rate limiting — 30 req/min default | YES |
| 5 | Rate limiting — 10 req/min on /generate-report | YES |
| 6 | HTTP 429 returned with retry_after on breach | YES |
| 7 | X-Content-Type-Options header present | YES |
| 8 | X-Frame-Options header present | YES |
| 9 | Content-Security-Policy header present | YES |
| 10 | X-XSS-Protection header present | YES |
| 11 | Server version hidden from response headers | YES |
| 12 | No hardcoded secrets in any file | YES |
| 13 | .env file in .gitignore | YES |
| 14 | All secrets loaded from environment variables | YES |
| 15 | PII audit completed — zero issues found | YES |
| 16 | OWASP ZAP baseline scan completed | YES |
| 17 | OWASP ZAP active scan completed | YES |
| 18 | Zero Critical findings remaining | YES |
| 19 | Zero High findings remaining | YES |
| 20 | All Medium findings resolved | YES |
| 21 | Residual Low risks documented and accepted | YES |
| 22 | Week 1 security tests — 100% pass | YES |
| 23 | Week 2 security sign-off — 92% pass | YES |
| 24 | Full stack security test — 88% pass | YES |
| 25 | AiServiceClient.java with graceful null return | YES |
| 26 | flask-talisman installed and configured | YES |
| 27 | All ZAP reports saved and committed | YES |
| 28 | SECURITY.md complete with all sections | YES |

**All 28 items complete! ✓**

---

### Team Sign-Off

| Role | Name | Signed |
|------|------|--------|
| AI Developer 3 | InsharahMuallim | SIGNED — 2 May 2026 |
| AI Developer 1 | Team Member | Pending |
| AI Developer 2 | Team Member | Pending |
| Java Developer 1 | Team Member | Pending |
| Java Developer 2 | Team Member | Pending |
| Java Developer 3 | Team Member | Pending |

---

### Demo Day Security Talking Points

**1. JWT Authentication (Java Backend)**
Spring Security enforces JWT on all endpoints. Requests without a valid token receive HTTP 401.

**2. Rate Limiting (Flask AI Service)**
flask-limiter blocks any IP sending more than 30 requests per minute. The /generate-report endpoint has a stricter limit of 10 requests per minute.

**3. Input Sanitisation**
All user input is sanitised before reaching the AI. HTML tags are stripped and 15+ prompt injection patterns are detected and blocked with HTTP 400.

**4. OWASP ZAP Results**
Three rounds of ZAP scanning conducted. Zero Critical or High findings remain. All Medium findings resolved using flask-talisman security headers.

---

*SECURITY.md Final Version — Day 15 — 2 May 2026 | AI Developer 3*
