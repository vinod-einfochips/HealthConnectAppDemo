# Security & Compliance Guide

## Overview

This document outlines the comprehensive security and compliance measures implemented in the HealthConnect Android application. The app handles sensitive health data and must comply with industry standards including HIPAA, GDPR, and Android security best practices.

---

## Table of Contents

1. [Security Architecture](#security-architecture)
2. [Data Protection](#data-protection)
3. [Network Security](#network-security)
4. [Code Obfuscation & ProGuard](#code-obfuscation--proguard)
5. [Permission Security](#permission-security)
6. [Quality & Compliance Automation](#quality--compliance-automation)
7. [Security Checklist](#security-checklist)
8. [Compliance Standards](#compliance-standards)
9. [Security Testing](#security-testing)
10. [Incident Response](#incident-response)

---

## Security Architecture

### Defense in Depth

The app implements multiple layers of security:

1. **Application Layer**: Code obfuscation, secure coding practices
2. **Data Layer**: Encryption at rest, secure backup exclusions
3. **Network Layer**: TLS 1.2+, certificate pinning, no cleartext traffic
4. **Platform Layer**: Android security features, permission management
5. **Build Layer**: ProGuard minification, security scanning

### Security Principles

- **Least Privilege**: Request only necessary permissions
- **Fail Secure**: Default to secure configurations
- **Defense in Depth**: Multiple security layers
- **Secure by Default**: Security enabled out-of-the-box
- **Privacy by Design**: Data protection built into architecture

---

## Data Protection

### 1. Backup & Restore Protection

**Configuration Files:**
- `app/src/main/res/xml/backup_rules.xml` (Android < 12)
- `app/src/main/res/xml/data_extraction_rules.xml` (Android 12+)

**Protected Data:**
- ✅ Shared Preferences (excluded from backup)
- ✅ Database files (excluded from backup)
- ✅ Internal storage (excluded from backup)
- ✅ Cache directory (excluded from backup)
- ✅ External storage (excluded from backup)

**Why?**
Health data is highly sensitive and must not be backed up to cloud services or transferred between devices without explicit user consent and proper encryption.

### 2. Data Storage Security

**Health Connect SDK:**
- All health data stored in Health Connect's secure storage
- Data encrypted at rest by the platform
- Access controlled via Android permissions
- Data isolated per application

**Best Practices:**
```kotlin
// ✅ GOOD: Use Health Connect for health data
healthConnectClient.insertRecords(records)

// ❌ BAD: Don't store health data in SharedPreferences
sharedPrefs.edit().putString("temperature", "98.6").apply()

// ❌ BAD: Don't store health data in plain files
File("health_data.txt").writeText("98.6")
```

### 3. Data Transmission Security

- All network traffic uses HTTPS (TLS 1.2+)
- Certificate pinning for production API
- No cleartext traffic allowed
- Secure WebSocket connections (WSS)

---

## Network Security

### Configuration

**File:** `app/src/main/res/xml/network_security_config.xml`

**Features:**
1. **Cleartext Traffic Disabled**: Only HTTPS connections allowed
2. **Certificate Pinning**: Production API uses certificate pinning
3. **TLS Version Enforcement**: TLS 1.2+ required
4. **Debug Overrides**: Localhost allowed in debug builds only

### Certificate Pinning

**Production API:**
```xml
<domain-config cleartextTrafficPermitted="false">
    <domain includeSubdomains="true">api.healthconnect.com</domain>
    <pin-set expiration="2026-12-31">
        <pin digest="SHA-256">PRIMARY_CERT_PIN_HERE</pin>
        <pin digest="SHA-256">BACKUP_CERT_PIN_HERE</pin>
    </pin-set>
</domain-config>
```

**Generate Certificate Pins:**
```bash
# Extract public key and generate SHA-256 pin
openssl x509 -in cert.pem -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  base64
```

### Network Security Best Practices

```kotlin
// ✅ GOOD: Use HTTPS
val url = "https://api.healthconnect.com/data"

// ❌ BAD: Never use HTTP for sensitive data
val url = "http://api.healthconnect.com/data"

// ✅ GOOD: Validate SSL certificates
// Network security config handles this automatically

// ❌ BAD: Don't disable certificate validation
// hostnameVerifier = { _, _ -> true } // NEVER DO THIS
```

---

## Code Obfuscation & ProGuard

### Configuration

**File:** `app/proguard-rules.pro`

**Enabled Features:**
1. **Code Obfuscation**: Class/method/field names obfuscated
2. **Code Shrinking**: Unused code removed
3. **Resource Shrinking**: Unused resources removed
4. **Optimization**: 5 optimization passes
5. **Log Removal**: Debug logs stripped in release builds

### Build Configuration

```kotlin
// app/build.gradle.kts
buildTypes {
    release {
        isMinifyEnabled = true           // Enable ProGuard
        isShrinkResources = true         // Remove unused resources
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Security Benefits

1. **Reverse Engineering Protection**: Obfuscated code harder to understand
2. **IP Protection**: Business logic obscured
3. **Attack Surface Reduction**: Unused code removed
4. **Performance**: Smaller APK, faster execution
5. **Log Security**: Debug logs removed from release builds

### ProGuard Rules Highlights

```proguard
# Aggressive obfuscation
-repackageclasses ''
-allowaccessmodification
-optimizationpasses 5

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Keep Health Connect classes
-keep class androidx.health.connect.client.** { *; }

# Keep Hilt/Dagger classes
-keep class dagger.hilt.** { *; }
```

---

## Permission Security

### Health Connect Permissions

**Required Permissions:**
```xml
<uses-permission android:name="android.permission.health.READ_BODY_TEMPERATURE"/>
<uses-permission android:name="android.permission.health.WRITE_BODY_TEMPERATURE"/>
```

### Permission Best Practices

1. **Request at Runtime**: Use `PermissionManager` for runtime requests
2. **Explain Why**: Show rationale before requesting
3. **Handle Denial**: Graceful degradation if denied
4. **Minimal Scope**: Request only necessary permissions
5. **Regular Review**: Audit permissions periodically

### Permission Manager Implementation

```kotlin
@Singleton
class PermissionManager @Inject constructor(
    private val healthConnectClient: HealthConnectClient
) {
    suspend fun checkPermissions(): Boolean {
        val granted = healthConnectClient.permissionController
            .getGrantedPermissions()
        return REQUIRED_PERMISSIONS.all { it in granted }
    }
    
    fun requestPermissions(activity: Activity) {
        val intent = PermissionController.createRequestPermissionResultContract()
        activity.startActivityForResult(intent, REQUEST_CODE)
    }
}
```

---

## Quality & Compliance Automation

### Pre-Commit Hooks

**Installation:**
```bash
# Install hooks
./scripts/install-hooks.sh

# Or manually
cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

**Automated Checks:**
1. ✅ Kotlin code formatting (ktlint)
2. ✅ Static code analysis (Detekt)
3. ✅ Android Lint
4. ✅ Unit tests
5. ✅ Security checks (hardcoded secrets, TODOs)

**Bypass (use sparingly):**
```bash
git commit --no-verify
```

### CI/CD Pipeline

**GitHub Actions Workflow:** `.github/workflows/ci-cd-build-test-report.yml`

**Pipeline Stages:**

1. **Build & Test**
   - Kotlin formatting check
   - Static code analysis
   - Unit tests (36 tests)
   - Code coverage (60%+ required)

2. **Android Lint**
   - Security checks
   - Performance checks
   - Best practices validation

3. **Security Scan**
   - Hardcoded secrets detection
   - ProGuard configuration verification
   - Network security config validation
   - Data protection rules verification
   - Dependency vulnerability scanning

4. **Build APK**
   - Debug APK generation
   - Artifact upload

5. **Quality Gate**
   - All checks must pass
   - Blocks merge if failed

**Scheduled Scans:**
- Daily security scans at 2 AM UTC
- Dependency vulnerability checks

### Manual Quality Checks

```bash
# Full quality check (recommended before release)
./gradlew qualityCheck

# Quick pre-commit check
./gradlew preCommitCheck

# Individual checks
./gradlew ktlintCheck        # Code formatting
./gradlew detekt             # Static analysis
./gradlew lintDevDebug       # Android Lint
./gradlew testDevDebugUnitTest  # Unit tests
./gradlew jacocoTestReport   # Code coverage
```

---

## Security Checklist

### Development Phase

- [ ] Use HTTPS for all network requests
- [ ] Implement certificate pinning for production
- [ ] Never hardcode API keys or secrets
- [ ] Use BuildConfig for environment-specific values
- [ ] Validate all user inputs
- [ ] Use parameterized queries (SQL injection prevention)
- [ ] Implement proper error handling (no sensitive info in errors)
- [ ] Remove debug logs before release
- [ ] Use secure random number generation
- [ ] Implement proper session management

### Build Phase

- [ ] Enable ProGuard for release builds
- [ ] Enable resource shrinking
- [ ] Remove unused code and resources
- [ ] Verify ProGuard rules are correct
- [ ] Test obfuscated release build
- [ ] Check APK size (should be smaller)
- [ ] Verify no debug code in release
- [ ] Sign APK with release keystore

### Release Phase

- [ ] Run full quality check (`./gradlew qualityCheck`)
- [ ] Run security scan
- [ ] Verify all tests pass
- [ ] Check code coverage (60%+ required)
- [ ] Review Android Lint report
- [ ] Test on multiple Android versions
- [ ] Verify permissions are minimal
- [ ] Test permission flows
- [ ] Verify data protection rules
- [ ] Test backup/restore behavior
- [ ] Verify network security config
- [ ] Test with network security enabled
- [ ] Review ProGuard mapping file
- [ ] Upload ProGuard mapping to Play Console

### Deployment Phase

- [ ] Use staged rollout (5% → 20% → 50% → 100%)
- [ ] Monitor crash reports
- [ ] Monitor security incidents
- [ ] Review user feedback
- [ ] Monitor API error rates
- [ ] Check for security vulnerabilities
- [ ] Update dependencies regularly
- [ ] Respond to security issues within 24 hours

---

## Compliance Standards

### HIPAA Compliance

**Health Insurance Portability and Accountability Act**

**Requirements:**
1. ✅ Data encryption at rest (Health Connect)
2. ✅ Data encryption in transit (TLS 1.2+)
3. ✅ Access controls (Android permissions)
4. ✅ Audit logging (Health Connect)
5. ✅ Data backup protection (excluded from backups)
6. ✅ Secure data disposal (Health Connect handles)

**Implementation:**
- Health data stored only in Health Connect
- No health data in SharedPreferences or files
- No health data in logs
- No health data in crash reports
- Secure network transmission (HTTPS)

### GDPR Compliance

**General Data Protection Regulation**

**Requirements:**
1. ✅ Data minimization (only necessary data collected)
2. ✅ Purpose limitation (data used only for stated purpose)
3. ✅ Storage limitation (user can delete data)
4. ✅ Data portability (export functionality)
5. ✅ Right to erasure (delete functionality)
6. ✅ Security measures (encryption, access control)
7. ✅ Privacy by design (built-in from start)

**Implementation:**
- User controls all health data
- Data deletion available
- Data export available (via Health Connect)
- No data sharing without consent
- Privacy policy provided
- Data processing transparency

### Android Security Best Practices

**Google Play Requirements:**
1. ✅ Target latest Android API (targetSdk 36)
2. ✅ Use AndroidX libraries
3. ✅ Implement network security config
4. ✅ Use SafetyNet/Play Integrity API
5. ✅ No cleartext traffic
6. ✅ Proper permission handling
7. ✅ Secure data storage
8. ✅ Code obfuscation enabled

---

## Security Testing

### Static Analysis

**Tools:**
- **Detekt**: Kotlin static analysis
- **ktlint**: Code formatting and style
- **Android Lint**: Android-specific checks

**Run:**
```bash
./gradlew detekt ktlintCheck lintDevDebug
```

### Dynamic Analysis

**Manual Testing:**
1. Test with network security enabled
2. Test permission flows
3. Test data backup/restore
4. Test on different Android versions
5. Test with ProGuard enabled

**Automated Testing:**
```bash
# Unit tests
./gradlew testDevDebugUnitTest

# Instrumented tests
./gradlew connectedDevDebugAndroidTest
```

### Penetration Testing

**Recommended Tools:**
- **MobSF**: Mobile Security Framework
- **QARK**: Quick Android Review Kit
- **Drozer**: Android security assessment framework

**Manual Checks:**
1. Decompile APK and review code
2. Intercept network traffic (should fail)
3. Attempt SQL injection
4. Test for hardcoded secrets
5. Check for insecure data storage

### Security Audit

**Quarterly Review:**
- [ ] Review all permissions
- [ ] Audit third-party dependencies
- [ ] Check for known vulnerabilities
- [ ] Review ProGuard rules
- [ ] Test security configurations
- [ ] Review access logs
- [ ] Update security documentation

---

## Incident Response

### Security Incident Procedure

**1. Detection**
- Monitor crash reports
- Review security scan results
- Monitor user reports
- Check Play Console alerts

**2. Assessment**
- Determine severity (Critical/High/Medium/Low)
- Identify affected users
- Assess data exposure risk
- Document incident details

**3. Containment**
- Disable affected features if needed
- Block compromised API keys
- Revoke compromised certificates
- Notify affected users if required

**4. Resolution**
- Fix security vulnerability
- Test fix thoroughly
- Deploy hotfix release
- Update security documentation

**5. Post-Incident**
- Conduct post-mortem
- Update security measures
- Improve detection mechanisms
- Train team on lessons learned

### Severity Levels

**Critical (P0):**
- Data breach or exposure
- Authentication bypass
- Remote code execution
- **Response Time**: Immediate (< 4 hours)

**High (P1):**
- Privilege escalation
- Sensitive data leakage
- Broken access control
- **Response Time**: < 24 hours

**Medium (P2):**
- Information disclosure
- Weak cryptography
- Insecure configuration
- **Response Time**: < 1 week

**Low (P3):**
- Code quality issues
- Minor security improvements
- Documentation updates
- **Response Time**: Next release

### Contact Information

**Security Team:**
- Email: security@healthconnect.com
- Emergency: +1-XXX-XXX-XXXX
- Bug Bounty: https://bugcrowd.com/healthconnect

---

## Quick Reference

### Security Commands

```bash
# Install pre-commit hooks
./scripts/install-hooks.sh

# Run all quality checks
./gradlew qualityCheck

# Run security-focused checks
./gradlew detekt lintDevDebug

# Build release APK with ProGuard
./gradlew assembleProdRelease

# Generate ProGuard mapping
./gradlew assembleProdRelease
# Mapping file: app/build/outputs/mapping/prodRelease/mapping.txt
```

### Security Files

| File | Purpose |
|------|---------|
| `app/proguard-rules.pro` | ProGuard obfuscation rules |
| `app/src/main/res/xml/network_security_config.xml` | Network security configuration |
| `app/src/main/res/xml/backup_rules.xml` | Backup exclusion rules (< Android 12) |
| `app/src/main/res/xml/data_extraction_rules.xml` | Data protection rules (Android 12+) |
| `scripts/pre-commit-hook.sh` | Pre-commit security checks |
| `.github/workflows/ci-cd-build-test-report.yml` | CI/CD security pipeline |

### Security Resources

- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)
- [Health Connect Security](https://developer.android.com/health-and-fitness/guides/health-connect/develop/security)
- [ProGuard Manual](https://www.guardsquare.com/manual/home)
- [Network Security Config](https://developer.android.com/training/articles/security-config)

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2024-11-14 | Initial security implementation |

---

## License

This security documentation is proprietary and confidential.
© 2024 HealthConnect. All rights reserved.
