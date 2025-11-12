# Code Quality Quick Reference Card

## 🚀 Quick Commands

### Most Used Commands
```bash
# Run all quality checks
./gradlew qualityCheck

# Quick pre-commit check
./gradlew preCommitCheck

# Auto-fix formatting
./gradlew ktlintFormat
```

### Individual Tools
```bash
# Formatting
./gradlew ktlintCheck          # Check formatting
./gradlew ktlintFormat         # Auto-fix formatting

# Static Analysis
./gradlew detekt               # Run Detekt
./gradlew detektBaseline       # Update baseline

# Lint
./gradlew lintDebug            # Run Android Lint
./gradlew lintDebug --update-baseline  # Update baseline

# Testing & Coverage
./gradlew testDebugUnitTest    # Run tests
./gradlew jacocoTestReport     # Generate coverage report
./gradlew jacocoTestCoverageVerification  # Verify coverage
```

## 📊 Report Locations

| Tool | Location |
|------|----------|
| **Detekt** | `build/reports/detekt/detekt.html` |
| **Lint** | `app/build/reports/lint/lint-results.html` |
| **Tests** | `app/build/reports/tests/testDebugUnitTest/index.html` |
| **Coverage** | `app/build/reports/jacoco/jacocoTestReport/html/index.html` |

## 📝 Configuration Files

- `.editorconfig` - Editor settings
- `.ktlint.yml` - ktlint rules
- `config/detekt/detekt.yml` - Detekt rules
- `app/lint-baseline.xml` - Lint baseline

## 🎯 Quality Thresholds

- **Code Coverage**: 60% minimum
- **Cyclomatic Complexity**: < 15
- **Method Length**: < 60 lines
- **Class Size**: < 600 lines

## 🐛 Common Fixes

### Formatting Issues
```bash
./gradlew ktlintFormat
```

### Suppress Detekt Rule
```kotlin
@Suppress("RuleName")
fun myFunction() { }
```

### Update Baselines
```bash
./gradlew detektBaseline
./gradlew lintDebug --update-baseline
```

## 🔄 Workflow

1. **Before Coding**: Pull latest changes
2. **During Coding**: Follow IDE suggestions
3. **Before Commit**: Run `./gradlew preCommitCheck` (optional)
4. **Before Push**: Run `./gradlew qualityCheck`

## 💡 Tips

- ✅ Run `ktlintFormat` regularly
- ✅ Fix issues as you code
- ✅ Write tests for new code
- ✅ Review reports weekly
- ❌ Don't suppress without understanding
- ❌ Don't ignore coverage drops

## 🆘 Need Help?

See full documentation: `docs/CODE_QUALITY_GUIDE.md`
