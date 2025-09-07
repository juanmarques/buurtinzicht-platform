#!/bin/bash

set -e

echo "🏗️ Building all Buurtinzicht services..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[BUILD]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Build backend (Spring Boot)
if [ -d "apps/backend" ]; then
    print_status "Building Spring Boot backend..."
    cd apps/backend
    ./mvnw clean package -DskipTests
    cd ../..
else
    print_status "Backend directory not found, skipping..."
fi

# Build frontend (Next.js)
if [ -d "apps/frontend" ]; then
    print_status "Building Next.js frontend..."
    cd apps/frontend
    npm run build
    cd ../..
else
    print_status "Frontend directory not found, skipping..."
fi

# Build ML service (Python)
if [ -d "apps/ml-service" ]; then
    print_status "Building Python ML service..."
    cd apps/ml-service
    if [ -f "pyproject.toml" ]; then
        poetry build
    else
        pip install -r requirements.txt
    fi
    cd ../..
else
    print_status "ML service directory not found, skipping..."
fi

# Build Docker images
print_status "Building Docker images..."

# Backend Docker image
if [ -f "apps/backend/Dockerfile" ]; then
    docker build -t buurtinzicht/backend:latest apps/backend/
fi

# Frontend Docker image
if [ -f "apps/frontend/Dockerfile" ]; then
    docker build -t buurtinzicht/frontend:latest apps/frontend/
fi

# ML service Docker image
if [ -f "apps/ml-service/Dockerfile" ]; then
    docker build -t buurtinzicht/ml-service:latest apps/ml-service/
fi

print_status "Build complete! 🎉"