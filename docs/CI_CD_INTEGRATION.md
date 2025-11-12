# CI/CD Integration with GitHub Actions

## Overview

This document describes the Continuous Integration and Continuous Deployment (CI/CD) pipeline implemented for the HealthConnect Android application using GitHub Actions.

---

## Table of Contents

1. [Pipeline Overview](#pipeline-overview)
2. [Workflow Configuration](#workflow-configuration)
3. [Pipeline Jobs](#pipeline-jobs)
4. [Triggers](#triggers)
5. [Quality Gates](#quality-gates)
6. [Artifacts](#artifacts)
7. [Pull Request Integration](#pull-request-integration)
8. [Setup Instructions](#setup-instructions)
9. [Troubleshooting](#troubleshooting)

---

## Pipeline Overview

### CI/CD Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    GitHub Actions Trigger                    │
│              (Push/PR to main/develop branches)              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────────┐
        │      Build & Test Job            │
        │  • Checkout code                 │
        │  • Setup JDK 17                  │
        │  • Run ktlint                    │
        │  • Run detekt                    │
        │  • Run unit tests (36 tests)    │
        │  • Generate coverage report      │
        │  • Verify coverage (60% min)     │
        │  • Upload artifacts              │
        └──────────────┬───────────────────┘
                       │
        ┌──────────────┴───────────────────┐
        │                                  │
        ▼                                  ▼
┌───────────────┐              ┌──────────────────┐
│  Lint Job     │              │  Build APK Job   │
│  • Run Lint   │              │  • Build debug   │
│  • Upload     │              │  • Upload APK    │
└───────┬───────┘              └────────┬─────────┘
        │                               │
        └───────────┬───────────────────┘
                    │
                    ▼
        ┌───────────────────────┐
        │   Quality Gate Job    │
        │  • Check all results  │
        │  • Pass/Fail pipeline │
        └───────────────────────┘
```

### Key Features

- ✅ **Automated Testing:** Runs 36 unit tests on every push/PR
- ✅ **Code Quality Checks:** ktlint, detekt, and Android Lint
- ✅ **Code Coverage:** JaCoCo with 60% minimum threshold
- ✅ **Artifact Management:** Test reports, coverage reports, APKs
- ✅ **PR Integration:** Automatic comments with test results and coverage
- ✅ **Quality Gates:** Pipeline fails if quality checks don't pass

---

## Workflow Configuration

### File Location
```
.github/workflows/ci-cd-build-test-report.yml
```

### Workflow Name
```yaml
name: CI-CD Pipeline
```

### Runtime Environment
- **OS:** Ubuntu Latest
- **JDK:** 17 (Temurin distribution)
- **Gradle:** Wrapper (cached)
- **Timeout:** 30 minutes (build-and-test), 20 minutes (other jobs)

---

## Pipeline Jobs

### 1. Build & Test Job

**Purpose:** Main quality assurance job that runs all tests and quality checks.

**Steps:**

#### a. Environment Setup
```yaml
- Checkout code (fetch-depth: 0 for full history)
- Set up JDK 17 with Gradle cache
- Grant execute permission for gradlew
- Cache Gradle packages
```

#### b. Code Quality Checks
```yaml
- Run ktlint (code formatting)
- Run detekt (static analysis)
```

#### c. Testing
```yaml
- Run unit tests (36 tests)
  Command: ./gradlew testDevDebugUnitTest
  
- Generate coverage report
  Command: ./gradlew jacocoTestReport
  
- Verify coverage thresholds
  Command: ./gradlew jacocoTestCoverageVerification
  Minimum: 60% overall, 50% per class
```

#### d. Artifact Upload
```yaml
- Test results (HTML reports)
- Coverage reports (HTML + XML)
- ktlint reports
- detekt reports
```

#### e. PR Integration (Pull Requests Only)
```yaml
- Comment test results on PR
- Add coverage report to PR
- Show coverage diff for changed files
```

**Failure Conditions:**
- ktlint check fails
- detekt check fails
- Any unit test fails
- Coverage verification fails (warning only)

---

### 2. Lint Job

**Purpose:** Run Android Lint checks for Android-specific issues.

**Steps:**
```yaml
- Checkout code
- Set up JDK 17
- Run Android Lint
  Command: ./gradlew lintDevDebug
  
- Upload lint reports (HTML + XML)
```

**Failure Conditions:**
- Critical lint errors (continues on warnings)

---

### 3. Build APK Job

**Purpose:** Build debug APK after all quality checks pass.

**Dependencies:** Requires `build-and-test` and `lint` jobs to succeed.

**Steps:**
```yaml
- Checkout code
- Set up JDK 17
- Build debug APK
  Command: ./gradlew assembleDevDebug
  
- Upload APK artifact
  Retention: 30 days
```

**Output:**
- `app-dev-debug.apk` (downloadable from Actions tab)

---

### 4. Quality Gate Job

**Purpose:** Final validation that all quality checks passed.

**Dependencies:** Requires `build-and-test` and `lint` jobs.

**Logic:**
```bash
if build-and-test.result != "success":
    fail pipeline
else:
    pass pipeline
```

**Status:** Always runs (even if previous jobs fail) to report final status.

---

## Triggers

### Automatic Triggers

#### 1. Push to Main/Develop
```yaml
on:
  push:
    branches: [ main, develop ]
```

**When:** Any commit pushed to `main` or `develop` branches.

**Actions:**
- Full pipeline execution
- All quality checks
- APK build
- Artifact upload

#### 2. Pull Request to Main/Develop
```yaml
on:
  pull_request:
    branches: [ main, develop ]
```

**When:** PR opened, updated, or synchronized.

**Actions:**
- Full pipeline execution
- Test results commented on PR
- Coverage report commented on PR
- Coverage diff for changed files

### Manual Trigger

```yaml
on:
  workflow_dispatch:
```

**How to Use:**
1. Go to Actions tab in GitHub
2. Select "CI-CD Pipeline"
3. Click "Run workflow"
4. Select branch
5. Click "Run workflow" button

---

## Quality Gates

### 1. Code Formatting (ktlint)

**Command:** `./gradlew ktlintCheck`

**Checks:**
- Kotlin code style compliance
- Consistent formatting
- Android Studio style guide

**Threshold:** 100% compliance (no violations)

**Failure:** Pipeline stops if violations found

---

### 2. Static Analysis (detekt)

**Command:** `./gradlew detekt`

**Checks:**
- Code complexity
- Potential bugs
- Code smells
- Best practices

**Threshold:** 0 violations

**Failure:** Pipeline stops if violations found

---

### 3. Unit Tests

**Command:** `./gradlew testDevDebugUnitTest`

**Tests:**
- 36 unit tests across 4 test classes
- TemperatureConverterTest (10 tests)
- TemperatureStatusTest (8 tests)
- RecordTemperatureUseCaseTest (8 tests)
- TemperatureViewModelTest (10 tests)

**Threshold:** 100% pass rate

**Failure:** Pipeline stops if any test fails

---

### 4. Code Coverage

**Command:** `./gradlew jacocoTestCoverageVerification`

**Thresholds:**
- Overall coverage: 60% minimum
- Per-class coverage: 50% minimum

**Failure:** Warning only (doesn't stop pipeline)

**Reports:** HTML and XML formats uploaded as artifacts

---

### 5. Android Lint

**Command:** `./gradlew lintDevDebug`

**Checks:**
- Android-specific issues
- Resource problems
- API usage
- Performance issues

**Threshold:** No critical errors

**Failure:** Continues on warnings

---

## Artifacts

### Artifact Retention

All artifacts are retained for **30 days** by default.

### Available Artifacts

#### 1. Test Results
**Name:** `test-results`

**Contents:**
- HTML test report
- Individual test results
- Test execution time

**Location:** `app/build/reports/tests/testDevDebugUnitTest/`

**Access:** Actions tab → Workflow run → Artifacts section

---

#### 2. Coverage Reports
**Name:** `coverage-reports`

**Contents:**
- HTML coverage report (browsable)
- XML coverage data (for tools)
- Line-by-line coverage

**Location:** `app/build/reports/jacoco/jacocoTestReport/`

**Access:** Actions tab → Workflow run → Artifacts section

---

#### 3. ktlint Reports
**Name:** `ktlint-reports`

**Contents:**
- Formatting violations
- File-by-file analysis

**Location:** `app/build/reports/ktlint/`

---

#### 4. detekt Reports
**Name:** `detekt-reports`

**Contents:**
- Static analysis results
- Code smell detection
- Complexity metrics

**Location:** `app/build/reports/detekt/`

---

#### 5. Lint Reports
**Name:** `lint-reports`

**Contents:**
- Android Lint HTML report
- Android Lint XML data

**Location:** `app/build/reports/lint/`

---

#### 6. Debug APK
**Name:** `app-dev-debug`

**Contents:**
- Signed debug APK
- Ready to install

**Location:** `app/build/outputs/apk/dev/debug/`

**Usage:** Download and install on device for testing

---

## Pull Request Integration

### Automatic PR Comments

#### 1. Unit Test Results

**Provider:** `EnricoMi/publish-unit-test-result-action@v2`

**Information Displayed:**
- Total tests run
- Tests passed/failed/skipped
- Test execution time
- Comparison with base branch

**Example:**
```
✅ Unit Test Results

36 tests   36 ✅   0 💤   0 ❌
Duration: 24s

All tests passed!
```

---

#### 2. Code Coverage Report

**Provider:** `madrapps/jacoco-report@v1.6.1`

**Information Displayed:**
- Overall coverage percentage
- Coverage for changed files
- Coverage diff vs base branch
- Pass/fail based on thresholds

**Thresholds:**
- Minimum overall: 60%
- Minimum changed files: 50%

**Example:**
```
📊 Code Coverage Report

Overall Coverage: 62.5% ✅
Changed Files: 75.0% ✅

Coverage increased by +2.3% 📈
```

---

## Setup Instructions

### Prerequisites

1. **GitHub Repository:** Project must be hosted on GitHub
2. **Repository Permissions:** Write access to create workflows
3. **Branch Protection:** (Optional) Configure branch protection rules

### Step 1: Verify Workflow File

Ensure the workflow file exists:
```
.github/workflows/ci-cd-build-test-report.yml
```

### Step 2: Enable GitHub Actions

1. Go to repository Settings
2. Navigate to Actions → General
3. Enable "Allow all actions and reusable workflows"
4. Save changes

### Step 3: Configure Branch Protection (Optional)

1. Go to Settings → Branches
2. Add rule for `main` branch
3. Enable "Require status checks to pass"
4. Select required checks:
   - Build & Test
   - Android Lint
   - Quality Gate

### Step 4: Test the Pipeline

#### Option A: Push to Branch
```bash
git checkout -b feature/test-ci
git commit --allow-empty -m "Test CI pipeline"
git push origin feature/test-ci
```

#### Option B: Manual Trigger
1. Go to Actions tab
2. Select "CI-CD Pipeline"
3. Click "Run workflow"
4. Select branch and run

### Step 5: Verify Results

1. Go to Actions tab
2. Click on the workflow run
3. Verify all jobs completed successfully
4. Download artifacts to inspect reports

---

## Troubleshooting

### Common Issues

#### 1. Gradle Permission Denied

**Error:**
```
Permission denied: ./gradlew
```

**Solution:**
The workflow includes this step automatically:
```yaml
- name: Grant execute permission for gradlew
  run: chmod +x gradlew
```

---

#### 2. Test Failures

**Error:**
```
Task :app:testDevDebugUnitTest FAILED
```

**Solution:**
1. Check test results artifact
2. Run tests locally: `./gradlew testDevDebugUnitTest`
3. Fix failing tests
4. Push changes

---

#### 3. Coverage Threshold Not Met

**Warning:**
```
Rule violated for bundle app: overall coverage 55% < 60%
```

**Solution:**
1. Add more unit tests
2. Improve existing test coverage
3. Run locally: `./gradlew jacocoTestReport`
4. View coverage report: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

---

#### 4. ktlint Violations

**Error:**
```
Ktlint found code style violations
```

**Solution:**
1. Auto-fix: `./gradlew ktlintFormat`
2. Commit changes
3. Push to trigger pipeline again

---

#### 5. detekt Violations

**Error:**
```
Detekt found code quality issues
```

**Solution:**
1. Review detekt report artifact
2. Fix reported issues
3. Or add to baseline: `config/detekt/baseline.xml`

---

#### 6. Artifact Upload Failures

**Error:**
```
actions/upload-artifact@v3 is deprecated
```

**Solution:**
All artifact uploads have been updated to v4:
```yaml
uses: actions/upload-artifact@v4
```

---

#### 7. Out of Memory

**Error:**
```
Java heap space OutOfMemoryError
```

**Solution:**
Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

---

## Pipeline Performance

### Typical Execution Times

| Job | Duration | Notes |
|-----|----------|-------|
| Build & Test | 5-8 minutes | Includes all quality checks |
| Lint | 2-3 minutes | Android Lint analysis |
| Build APK | 2-3 minutes | Debug build only |
| Quality Gate | < 1 minute | Status check only |
| **Total** | **10-15 minutes** | Full pipeline |

### Optimization Tips

1. **Gradle Caching:** Already enabled in workflow
2. **Dependency Caching:** Already enabled in workflow
3. **Parallel Jobs:** Lint runs in parallel with Build & Test
4. **Incremental Builds:** Gradle handles automatically

---

## Best Practices

### 1. Commit Messages

Use conventional commits for better changelog:
```
feat: add temperature conversion feature
fix: resolve null pointer in ViewModel
test: add unit tests for TemperatureConverter
docs: update CI/CD documentation
```

### 2. Branch Strategy

- `main`: Production-ready code
- `develop`: Integration branch
- `feature/*`: Feature branches
- `bugfix/*`: Bug fix branches

### 3. Pull Request Workflow

1. Create feature branch from `develop`
2. Make changes and commit
3. Push branch and create PR
4. Wait for CI checks to pass
5. Request code review
6. Merge after approval and passing checks

### 4. Monitoring

- Check Actions tab regularly
- Review failed pipelines immediately
- Monitor artifact sizes
- Track pipeline execution times

---

## Security Considerations

### Secrets Management

**Current:** No secrets required for basic pipeline

**Future:** If adding signing or deployment:
```yaml
env:
  KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
  KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
  KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### Permissions

Workflow uses default `GITHUB_TOKEN` with minimal permissions:
- Read repository
- Write checks (for PR comments)
- Upload artifacts

---

## Future Enhancements

### Planned Improvements

1. **Release Pipeline**
   - Automated versioning
   - Signed release builds
   - Play Store deployment

2. **Instrumentation Tests**
   - UI tests with Espresso
   - Integration tests
   - Screenshot tests

3. **Dependency Scanning**
   - OWASP dependency check
   - Vulnerability scanning
   - License compliance

4. **Performance Testing**
   - APK size tracking
   - Build time monitoring
   - Memory profiling

5. **Notifications**
   - Slack integration
   - Email notifications
   - GitHub Discussions posts

---

## Summary

The CI/CD pipeline provides:

- ✅ **Automated quality assurance** on every push/PR
- ✅ **36 unit tests** with 100% pass rate
- ✅ **60% code coverage** minimum threshold
- ✅ **Code quality checks** (ktlint, detekt, lint)
- ✅ **Artifact management** (reports, APKs)
- ✅ **PR integration** (automatic comments)
- ✅ **Quality gates** (fail fast on issues)

**Pipeline Status:** ✅ Active and Operational

**Execution Time:** ~10-15 minutes per run

**Reliability:** High (with proper test coverage)

---

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [actions/checkout](https://github.com/actions/checkout)
- [actions/setup-java](https://github.com/actions/setup-java)
- [actions/upload-artifact](https://github.com/actions/upload-artifact)
- [EnricoMi/publish-unit-test-result-action](https://github.com/EnricoMi/publish-unit-test-result-action)
- [madrapps/jacoco-report](https://github.com/madrapps/jacoco-report)

---

**Last Updated:** 2025-01-13  
**Version:** 1.0  
**Author:** HealthConnect Development Team
