# Security Policy

## 🔒 Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 2.0.x   | :white_check_mark: |
| 1.x.x   | :x:                |

## 🛡️ Security Features

CarCare Pro implements several security measures:

### Authentication & Authorization
- **BCrypt Password Hashing** - Industry-standard password hashing with salt
- **Role-based Access Control** - Admin, Manager, and Employee roles
- **Session Management** - Secure session handling

### Data Protection
- **HikariCP Connection Pool** - Secure database connection management
- **Prepared Statements** - Prevention of SQL injection attacks
- **Input Validation** - Comprehensive input sanitization

### Best Practices
- No hardcoded credentials in source code
- Configuration externalized to `config.properties`
- Sensitive data excluded from version control

## 🚨 Reporting a Vulnerability

We take security seriously. If you discover a security vulnerability, please follow these steps:

### Do NOT
- ❌ Open a public GitHub issue
- ❌ Discuss in public channels
- ❌ Exploit the vulnerability

### Do
1. **Email us directly** at [security@example.com]
2. **Include details:**
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

### What to Expect
| Timeline | Action |
|----------|--------|
| 24 hours | Acknowledgment of your report |
| 48 hours | Initial assessment |
| 7 days | Status update and timeline |
| 30 days | Resolution target (varies by severity) |

### Severity Levels

| Level | Description | Response Time |
|-------|-------------|---------------|
| 🔴 Critical | System compromise, data breach | Immediate |
| 🟠 High | Authentication bypass, injection | 24-48 hours |
| 🟡 Medium | Limited data exposure | 1 week |
| 🟢 Low | Minor issues | 2-4 weeks |

## 🎁 Recognition

We appreciate security researchers who help keep CarCare Pro secure:

- Your name in our security acknowledgments (if desired)
- Credit in release notes for fixes
- Our sincere gratitude!

## 📋 Security Checklist for Contributors

When contributing code, please ensure:

- [ ] No hardcoded credentials or secrets
- [ ] Use parameterized queries for database operations
- [ ] Validate and sanitize all user inputs
- [ ] Follow the principle of least privilege
- [ ] Keep dependencies up to date
- [ ] Use secure communication (HTTPS/TLS)
- [ ] Handle errors without exposing sensitive information

## 📚 Security Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Java Security Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)
- [BCrypt Best Practices](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

---

Thank you for helping keep CarCare Pro and its users safe! 🙏
