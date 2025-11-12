# Code Quality Guide

This document provides comprehensive information about the code quality tools integrated into the HealthConnect project.

## 📋 Table of Contents

- [Overview](#overview)
- [Tools Integrated](#tools-integrated)
- [Quick Start](#quick-start)
- [Detailed Tool Configuration](#detailed-tool-configuration)
- [Running Quality Checks](#running-quality-checks)
- [CI/CD Integration](#cicd-integration)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

The project uses multiple code quality tools to ensure:
- **Code Consistency**: Uniform code style across the codebase
- **Bug Prevention**: Early detection of potential issues
- **Maintainability**: Clean, readable, and maintainable code
- **Test Coverage**: Adequate test coverage for reliability
- **Continuous Improvement**: Automated quality checks in CI/CD

## 🛠️ Tools Integrated

### 1. **Detekt** - Static Code Analysis
- **Purpose**: Detect code smells, complexity issues, and potential bugs
- **Version**: 1.23.4
- **Configuration**: `config/detekt/detekt.yml`

### 2. **ktlint** - Kotlin Linter & Formatter
- **Purpose**: Enforce Kotlin coding conventions and style
- **Version**: 1.0.1 (via plugin 12.0.3)
- **Configuration**: `.ktlint.yml` and `.editorconfig`

### 3. **Android Lint** - Android-specific Analysis
- **Purpose**: Detect Android-specific issues, performance problems, and security vulnerabilities
- **Configuration**: Configured in `app/build.gradle.kts`
- **Baseline**: `app/lint-baseline.xml`

### 4. **JaCoCo** - Code Coverage
- **Purpose**: Measure test coverage and ensure quality thresholds
- **Version**: 0.8.11
- **Minimum Coverage**: 60% overall, 50% per class

## 🚀 Quick Start

### Initial Setup

1. **Install Git Hooks** (Recommended):
   ```bash
   chmod +x scripts/setup-hooks.sh
   ./scripts/setup-hooks.sh
   ```

2. **Run All Quality Checks**:
   ```bash
   ./gradlew qualityCheck
   ```

3. **Auto-fix Formatting Issues**:
   ```bash
   ./gradlew ktlintFormat
   ```

## 📖 Detailed Tool Configuration

### Detekt Configuration

**Location**: `config/detekt/detekt.yml`

**Key Rules Enabled**:
- Complexity checks (cyclomatic complexity, long methods)
- Coroutine best practices
- Exception handling
- Naming conventions
- Performance optimizations
- Style consistency

**Customization**:
```yaml
complexity:
  CyclomaticComplexMethod:
    threshold: 15
  LongMethod:
    threshold: 60
```

**Generate Baseline** (to suppress existing issues):
```bash
./gradlew detektBaseline
```

### ktlint Configuration

**Location**: `.ktlint.yml` and `.editorconfig`

**Key Settings**:
- Indent: 4 spaces
- Max line length: 120 characters
- Trailing commas: Enabled
- Code style: Android Studio

**Auto-format Code**:
```bash
# Format all Kotlin files
./gradlew ktlintFormat

# Check without fixing
./gradlew ktlintCheck
```

### Android Lint Configuration

**Key Features**:
- Abort on error: Enabled
- Check all warnings: Enabled
- Check dependencies: Enabled
- Multiple report formats: HTML, XML, Text

**Update Baseline**:
```bash
./gradlew lintDebug --update-baseline
```

**View Reports**:
- HTML: `app/build/reports/lint/lint-results.html`
- XML: `app/build/reports/lint/lint-results.xml`

### JaCoCo Configuration

**Coverage Thresholds**:
- Overall: 60%
- Per Class: 50%

**Excluded from Coverage**:
- Generated code (R.class, BuildConfig, Dagger/Hilt)
- Data models
- Dependency injection modules

**Generate Coverage Report**:
```bash
./gradlew jacocoTestReport
```

**View Report**:
Open `app/build/reports/jacoco/jacocoTestReport/html/index.html`

## 🔄 Running Quality Checks

### Individual Tools

```bash
# ktlint - Code formatting
./gradlew ktlintCheck
./gradlew ktlintFormat

# Detekt - Static analysis
./gradlew detekt

# Android Lint
./gradlew lintDebug

# Unit Tests
./gradlew testDebugUnitTest

# Code Coverage
./gradlew jacocoTestReport
./gradlew jacocoTestCoverageVerification
```

### Combined Tasks

```bash
# Full quality check (all tools + coverage)
./gradlew qualityCheck

# Quick pre-commit check (no coverage)
./gradlew preCommitCheck

# Project-level quality check
./gradlew :qualityCheck
```

### Gradle Task Hierarchy

```
qualityCheck
├── testDebugUnitTest
├── detekt
├── ktlintCheck
├── lint
├── jacocoTestReport
└── jacocoTestCoverageVerification

preCommitCheck
├── ktlintCheck
├── detekt
└── testDebugUnitTest
```

## 🔁 CI/CD Integration

### GitHub Actions

**Workflow**: `.github/workflows/code-quality.yml`

**Triggers**:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`

**Checks Performed**:
1. ktlint formatting
2. Detekt static analysis
3. Android Lint
4. Unit tests
5. Code coverage generation
6. Coverage verification

**Artifacts Generated**:
- Detekt report
- Lint report
- Test results
- Coverage report
- Debug APK

### Local Pre-commit Hooks

**Setup**:
```bash
./scripts/setup-hooks.sh
```

**What it does**:
- Runs ktlint, Detekt, and unit tests before each commit
- Prevents commits if checks fail
- Can be bypassed with `git commit --no-verify` (not recommended)

## 💡 Best Practices

### 1. Run Checks Before Committing
```bash
./gradlew preCommitCheck
```

### 2. Fix Formatting Issues Automatically
```bash
./gradlew ktlintFormat
```

### 3. Review Detekt Issues
- Don't suppress warnings without understanding them
- Use `@Suppress` annotations sparingly
- Update baseline only for legacy code

### 4. Maintain Test Coverage
- Write tests for new features
- Aim for >70% coverage on business logic
- Exclude only truly untestable code

### 5. Keep Baselines Updated
```bash
# Detekt baseline
./gradlew detektBaseline

# Lint baseline
./gradlew lintDebug --update-baseline
```

### 6. Monitor Reports Regularly
- Review HTML reports for detailed insights
- Track trends over time
- Address issues incrementally

## 🔧 Troubleshooting

### Common Issues

#### 1. ktlint Formatting Conflicts
**Problem**: ktlint and IDE formatting differ

**Solution**:
- Import `.editorconfig` in your IDE
- Use ktlint's auto-format: `./gradlew ktlintFormat`
- Configure IDE to use EditorConfig settings

#### 2. Detekt False Positives
**Problem**: Detekt reports issues that are acceptable

**Solution**:
```kotlin
@Suppress("RuleName")
fun myFunction() { ... }
```
Or update `config/detekt/detekt.yml` to adjust thresholds

#### 3. Coverage Threshold Failures
**Problem**: Coverage below minimum threshold

**Solution**:
- Write more tests
- Temporarily adjust thresholds in `app/build.gradle.kts`
- Exclude untestable code appropriately

#### 4. Lint Baseline Issues
**Problem**: Lint finds issues already in baseline

**Solution**:
```bash
./gradlew lintDebug --update-baseline
```

#### 5. Build Performance
**Problem**: Quality checks slow down build

**Solution**:
- Use `preCommitCheck` for quick checks
- Run full `qualityCheck` before pushing
- Enable Gradle build cache
- Use parallel execution

### Performance Optimization

```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.jvmargs=-Xmx4096m
```

## 📊 Report Locations

| Tool | Report Location |
|------|----------------|
| Detekt | `build/reports/detekt/detekt.html` |
| ktlint | Console output |
| Android Lint | `app/build/reports/lint/lint-results.html` |
| Unit Tests | `app/build/reports/tests/testDebugUnitTest/index.html` |
| JaCoCo Coverage | `app/build/reports/jacoco/jacocoTestReport/html/index.html` |

## 🎓 Learning Resources

### Detekt
- [Official Documentation](https://detekt.dev/)
- [Rule Set Reference](https://detekt.dev/docs/rules/complexity)

### ktlint
- [Official Documentation](https://pinterest.github.io/ktlint/)
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)

### Android Lint
- [Official Documentation](https://developer.android.com/studio/write/lint)
- [Lint Check Reference](https://googlesamples.github.io/android-custom-lint-rules/checks/index.html)

### JaCoCo
- [Official Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Coverage Metrics](https://www.jacoco.org/jacoco/trunk/doc/counters.html)

## 🔄 Continuous Improvement

### Regular Maintenance Tasks

**Weekly**:
- Review quality check reports
- Address new Detekt/Lint issues
- Monitor coverage trends

**Monthly**:
- Update tool versions
- Review and adjust thresholds
- Clean up suppressed warnings

**Quarterly**:
- Audit baseline files
- Review and update rules
- Team training on new practices

## 📝 Configuration Files Reference

```
HealthConnectDemo/
├── .editorconfig                    # Editor configuration
├── .ktlint.yml                      # ktlint configuration
├── config/
│   └── detekt/
│       ├── detekt.yml              # Detekt rules
│       └── baseline.xml            # Detekt baseline
├── app/
│   ├── build.gradle.kts            # JaCoCo & Lint config
│   └── lint-baseline.xml           # Lint baseline
├── scripts/
│   ├── pre-commit-hook.sh          # Pre-commit checks
│   └── setup-hooks.sh              # Hook installation
└── .github/
    └── workflows/
        └── code-quality.yml        # CI/CD workflow
```

## 🎯 Quality Metrics Goals

| Metric | Current Target | Stretch Goal |
|--------|---------------|--------------|
| Code Coverage | 60% | 80% |
| Detekt Issues | 0 | 0 |
| Lint Warnings | < 10 | 0 |
| Cyclomatic Complexity | < 15 | < 10 |
| Max Method Length | < 60 lines | < 40 lines |

---

**Last Updated**: November 2024
**Maintained By**: Development Team
