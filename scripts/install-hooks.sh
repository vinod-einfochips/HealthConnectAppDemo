#!/bin/bash

################################################################################
# Git Hooks Installation Script
################################################################################
# This script installs pre-commit hooks for the HealthConnect project.
# Run this once after cloning the repository.
################################################################################

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Installing Git Hooks for HealthConnect${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"

# Check if .git directory exists
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}⚠ .git directory not found. Are you in the project root?${NC}"
    exit 1
fi

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Install pre-commit hook
if [ -f "scripts/pre-commit-hook.sh" ]; then
    cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
    chmod +x .git/hooks/pre-commit
    echo -e "${GREEN}✓ Pre-commit hook installed${NC}"
else
    echo -e "${YELLOW}⚠ scripts/pre-commit-hook.sh not found${NC}"
    exit 1
fi

echo -e "\n${GREEN}✓ Git hooks installed successfully!${NC}\n"
echo -e "${BLUE}The following checks will run before each commit:${NC}"
echo "  1. Kotlin code formatting (ktlint)"
echo "  2. Static code analysis (Detekt)"
echo "  3. Android Lint"
echo "  4. Unit tests"
echo "  5. Security checks"
echo ""
echo -e "${BLUE}To bypass hooks (use sparingly):${NC}"
echo "  git commit --no-verify"
echo ""
echo -e "${BLUE}To skip specific checks:${NC}"
echo "  SKIP_TESTS=true git commit"
echo "  SKIP_LINT=true git commit"
echo ""

exit 0
