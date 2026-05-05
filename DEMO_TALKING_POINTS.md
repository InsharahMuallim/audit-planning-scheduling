# Security Talking Points — Demo Day
**Tool-21: Audit Planning and Scheduling**

---

## 1. JWT Authentication
The Java backend uses Spring Security with JWT tokens. Every API request requires a valid token. Requests without a token receive HTTP 401 Unauthorized. Tokens expire after a set time to prevent reuse.

## 2. Rate Limiting
The Flask AI service uses flask-limiter to prevent abuse. All endpoints are limited to 30 requests per minute per IP address. The /generate-report endpoint has a stricter limit of 10 requests per minute. Exceeding the limit returns HTTP 429 with a retry_after field.

## 3. Input Sanitisation
All user input is sanitised before reaching the AI. The middleware strips HTML tags to prevent XSS attacks and detects 15 prompt injection patterns. Malicious input returns HTTP 400 with a clear error message.

## 4. OWASP ZAP Results
Three rounds of OWASP ZAP security scanning were conducted. The initial scan identified 4 findings. After implementing flask-talisman security headers all Medium findings were resolved. The final scan confirms zero Critical and zero High vulnerabilities remaining.
