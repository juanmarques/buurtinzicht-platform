#!/bin/bash

set -e

echo "🧪 Running all tests for Buurtinzicht platform..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[TEST]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Function to run tests with error handling
run_test() {
    local service=$1
    local command=$2
    print_status "Running $service tests..."
    if eval "$command"; then
        print_status "$service tests passed ✅"
        return 0
    else
        print_error "$service tests failed ❌"
        return 1
    fi
}

# Test results tracking
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Backend tests (Spring Boot)
if [ -d "apps/backend" ]; then
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if run_test "Backend" "cd apps/backend && ./mvnw test"; then
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    print_warning "Backend directory not found, skipping backend tests..."
fi

# Frontend tests (Jest + Playwright)
if [ -d "apps/frontend" ]; then
    TOTAL_TESTS=$((TOTAL_TESTS + 2))
    
    # Unit tests with Jest
    if run_test "Frontend Unit" "cd apps/frontend && npm test"; then
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
    
    # E2E tests with Playwright
    if run_test "Frontend E2E" "cd apps/frontend && npm run test:e2e"; then
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    print_warning "Frontend directory not found, skipping frontend tests..."
fi

# ML service tests (pytest)
if [ -d "apps/ml-service" ]; then
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ -f "apps/ml-service/pyproject.toml" ]; then
        if run_test "ML Service" "cd apps/ml-service && poetry run pytest"; then
            PASSED_TESTS=$((PASSED_TESTS + 1))
        else
            FAILED_TESTS=$((FAILED_TESTS + 1))
        fi
    else
        if run_test "ML Service" "cd apps/ml-service && python -m pytest"; then
            PASSED_TESTS=$((PASSED_TESTS + 1))
        else
            FAILED_TESTS=$((FAILED_TESTS + 1))
        fi
    fi
else
    print_warning "ML service directory not found, skipping ML service tests..."
fi

# Integration tests
print_status "Running integration tests..."
if [ -d "tests/integration" ]; then
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if run_test "Integration" "cd tests/integration && npm test"; then
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    print_warning "Integration tests directory not found, skipping integration tests..."
fi

# Test summary
echo ""
print_status "Test Summary:"
print_status "  Total test suites: $TOTAL_TESTS"
print_status "  Passed: $PASSED_TESTS ✅"
if [ $FAILED_TESTS -gt 0 ]; then
    print_error "  Failed: $FAILED_TESTS ❌"
    echo ""
    print_error "Some tests failed. Please check the output above."
    exit 1
else
    print_status "  Failed: $FAILED_TESTS"
    echo ""
    print_status "All tests passed! 🎉"
    exit 0
fi