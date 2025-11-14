# Security & Compliance - Quick Reference

## 🚀 Quick Start

### Install Pre-Commit Hooks
```bash
./scripts/install-hooks.sh
```

### Run Quality Checks
```bash
# Full check (before release)
./gradlew qualityCheck

# Quick check (before commit)
./gradlew preCommitCheck
```

### Build Release APK
```bash
./gradlew assembleProdRelease
```

---

## ✅ Security Checklist

### Before Every Commit
- [ ] Run `./gradlew preCommitCheck`
- [ ] No hardcoded secrets
- [ ] No sensitive data in logs
- [ ] Tests pass

### Before Every Release
- [ ] Run `./gradlew qualityCheck`
- [ ] ProGuard enabled
- [ ] All tests pass (36/36)
- [ ] Code coverage ≥ 60%
- [ ] Android Lint clean
- [ ] Security scan passed

---

## 🔒 Security Features

### Data Protection
✅ **Backup Exclusions**: Health data excluded from backups  
✅ **Encryption**: Data encrypted at rest (Health Connect)  
✅ **No Cloud Sync**: Sensitive data not synced to cloud  

### Network Security
✅ **HTTPS Only**: No cleartext traffic allowed  
✅ **Certificate Pinning**: Production API pinned  
✅ **TLS 1.2+**: Modern encryption enforced  

### Code Protection
✅ **ProGuard**: Code obfuscated in release builds  
✅ **Log Removal**: Debug logs stripped from release  
✅ **Resource Shrinking**: Unused resources removed  

---

## 📋 Key Commands

### Quality Checks
```bash
./gradlew ktlintCheck        # Code formatting
./gradlew detekt             # Static analysis
./gradlew lintDevDebug       # Android Lint
./gradlew testDevDebugUnitTest  # Unit tests
./gradlew jacocoTestReport   # Coverage report
```

### Build Commands
```bash
./gradlew assembleDevDebug      # Debug build
./gradlew assembleProdRelease   # Release build (ProGuard)
./gradlew bundleProdRelease     # Release bundle (Play Store)
```

### Fix Commands
```bash
./gradlew ktlintFormat       # Auto-fix formatting
```

---

## 📁 Security Files

| File | Purpose |
|------|---------|
| `app/proguard-rules.pro` | ProGuard rules |
| `app/src/main/res/xml/network_security_config.xml` | Network security |
| `app/src/main/res/xml/backup_rules.xml` | Backup rules |
| `app/src/main/res/xml/data_extraction_rules.xml` | Data protection |
| `scripts/pre-commit-hook.sh` | Pre-commit checks |

---

## 🚨 Common Issues

### ProGuard Build Fails
```bash
# Check ProGuard rules
cat app/proguard-rules.pro

# Build with stack trace
./gradlew assembleProdRelease --stacktrace
```

### Tests Fail
```bash
# Run specific test
./gradlew test --tests "TemperatureConverterTest"

# View test report
open app/build/reports/tests/testDevDebugUnitTest/index.html
```

### Lint Errors
```bash
# View lint report
open app/build/reports/lint/lint-results.html

# Update baseline (if needed)
./gradlew lintDevDebug --continue
```

---

## 🔐 Security Best Practices

### ✅ DO
- Use HTTPS for all network requests
- Store secrets in BuildConfig or secure storage
- Request minimal permissions
- Validate all user inputs
- Use Health Connect for health data
- Enable ProGuard for release builds
- Run security checks before commit

### ❌ DON'T
- Hardcode API keys or secrets
- Use HTTP for sensitive data
- Store health data in SharedPreferences
- Log sensitive information
- Disable certificate validation
- Skip security checks
- Commit without running tests

---

## 📊 Compliance

### HIPAA ✅
- Data encrypted at rest and in transit
- Access controls via permissions
- No health data in backups
- Audit logging (Health Connect)

### GDPR ✅
- Data minimization
- User controls data
- Data deletion available
- Privacy by design

### Android Best Practices ✅
- Target SDK 36
- Network security config
- ProGuard enabled
- Secure data storage

---

## 🔗 Quick Links

- **Full Documentation**: [SECURITY_AND_COMPLIANCE.md](./SECURITY_AND_COMPLIANCE.md)
- **Architecture**: [ARCHITECTURE.md](./ARCHITECTURE.md)
- **Testing**: [TESTING_FRAMEWORK_SUMMARY.md](./TESTING_FRAMEWORK_SUMMARY.md)
- **Code Quality**: [CODE_QUALITY_SUMMARY.md](./CODE_QUALITY_SUMMARY.md)

---

## 📞 Support

**Security Issues**: security@healthconnect.com  
**Bug Reports**: GitHub Issues  
**Documentation**: `/docs` directory

---

**Last Updated**: 2024-11-14  
**Version**: 1.0.0
