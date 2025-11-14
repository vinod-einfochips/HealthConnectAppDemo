#!/bin/bash

################################################################################
# Pre-Commit Hook for HealthConnect App
################################################################################
# This script runs automated quality and security checks before each commit.
# It ensures code quality, security compliance, and prevents committing
# broken or non-compliant code.
#
# Installation:
#   cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
#   chmod +x .git/hooks/pre-commit
#
# To bypass (use sparingly):
#   git commit --no-verify
################################################################################

set -e  # Exit on first error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SKIP_TESTS=${SKIP_TESTS:-false}
SKIP_LINT=${SKIP_LINT:-false}

################################################################################
# Helper Functions
################################################################################

print_header() {
    echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

################################################################################
# Pre-flight Checks
################################################################################

print_header "Pre-Commit Quality & Security Checks"

# Check if we're in the project root
if [ ! -f "gradlew" ]; then
    print_error "gradlew not found. Please run this script from the project root."
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

################################################################################
# 1. Kotlin Code Formatting (ktlint)
################################################################################

print_header "1/5 - Kotlin Code Formatting (ktlint)"

if ./gradlew ktlintCheck --console=plain --quiet; then
    print_success "Kotlin formatting check passed"
else
    print_error "Kotlin formatting check failed"
    print_info "Run './gradlew ktlintFormat' to auto-fix formatting issues"
    exit 1
fi

################################################################################
# 2. Static Code Analysis (Detekt)
################################################################################

print_header "2/5 - Static Code Analysis (Detekt)"

if ./gradlew detekt --console=plain --quiet; then
    print_success "Static code analysis passed"
else
    print_error "Static code analysis failed"
    print_info "Check the report at: app/build/reports/detekt/detekt.html"
    exit 1
fi

################################################################################
# 3. Android Lint (Security & Best Practices)
################################################################################

print_header "3/5 - Android Lint Analysis"

if [ "$SKIP_LINT" = "true" ]; then
    print_warning "Android Lint skipped (SKIP_LINT=true)"
else
    if ./gradlew lintDevDebug --console=plain --quiet; then
        print_success "Android Lint check passed"
    else
        print_warning "Android Lint found issues"
        print_info "Check the report at: app/build/reports/lint/lint-results.html"
        print_info "Continuing... (Lint warnings don't block commits)"
    fi
fi

################################################################################
# 4. Unit Tests
################################################################################

print_header "4/5 - Unit Tests"

if [ "$SKIP_TESTS" = "true" ]; then
    print_warning "Unit tests skipped (SKIP_TESTS=true)"
else
    if ./gradlew testDevDebugUnitTest --console=plain --quiet; then
        print_success "All unit tests passed"
    else
        print_error "Unit tests failed"
        print_info "Check the report at: app/build/reports/tests/testDevDebugUnitTest/index.html"
        exit 1
    fi
fi

################################################################################
# 5. Security Checks
################################################################################

print_header "5/5 - Security Checks"

# Check for hardcoded secrets
print_info "Checking for hardcoded secrets..."
if git diff --cached --name-only | grep -E '\.(kt|java|xml|properties)$' | xargs grep -nE '(password|secret|api_key|token|private_key)\s*=\s*["\'][^"\']+["\']' 2>/dev/null; then
    print_error "Potential hardcoded secrets found!"
    print_info "Please remove hardcoded credentials and use secure storage"
    exit 1
else
    print_success "No hardcoded secrets detected"
fi

# Check for TODO/FIXME in committed code
print_info "Checking for unresolved TODOs/FIXMEs..."
TODO_COUNT=$(git diff --cached --name-only | grep -E '\.(kt|java)$' | xargs grep -nE 'TODO|FIXME' 2>/dev/null | wc -l || echo 0)
if [ "$TODO_COUNT" -gt 0 ]; then
    print_warning "Found $TODO_COUNT TODO/FIXME comments in staged files"
    print_info "Consider resolving them before committing"
fi

# Check ProGuard rules exist for release
if [ -f "app/proguard-rules.pro" ]; then
    print_success "ProGuard rules file exists"
else
    print_warning "ProGuard rules file not found"
fi

# Check network security config
if [ -f "app/src/main/res/xml/network_security_config.xml" ]; then
    print_success "Network security config exists"
else
    print_warning "Network security config not found"
fi

################################################################################
# Summary
################################################################################

print_header "Pre-Commit Checks Complete"

print_success "All quality and security checks passed!"
print_info "Proceeding with commit..."

exit 0
