#!/bin/bash

set -e

echo "🏗️ Setting up Buurtinzicht development environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed and running
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

if ! docker info &> /dev/null; then
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    print_error "Docker Compose is not available. Please install Docker Compose first."
    exit 1
fi

# Check if Node.js is installed (for frontend development)
if command -v node &> /dev/null; then
    NODE_VERSION=$(node --version)
    print_status "Node.js version: $NODE_VERSION"
else
    print_warning "Node.js is not installed. Some development features may not work."
fi

# Check if Java is installed (for backend development)
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java --version | head -n1)
    print_status "Java version: $JAVA_VERSION"
else
    print_warning "Java is not installed. Backend development will require Java 21+"
fi

# Check if Python is installed (for ML service)
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version)
    print_status "Python version: $PYTHON_VERSION"
else
    print_warning "Python 3 is not installed. ML service development will require Python 3.11+"
fi

# Create necessary directories
print_status "Creating project directories..."
mkdir -p infrastructure/sql/init
mkdir -p infrastructure/monitoring
mkdir -p logs
mkdir -p data/uploads
mkdir -p data/exports

# Create SQL initialization script
print_status "Creating database initialization script..."
cat > infrastructure/sql/init/01-init.sql << 'EOF'
-- Create Keycloak database
CREATE DATABASE keycloak;

-- Create extensions for main database
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Create application user
CREATE USER app_user WITH ENCRYPTED PASSWORD 'app_password';
GRANT CONNECT ON DATABASE buurtinzicht TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT CREATE ON SCHEMA public TO app_user;

-- Grant permissions on Keycloak database
GRANT ALL PRIVILEGES ON DATABASE keycloak TO buurtinzicht;
EOF

# Create Prometheus configuration
print_status "Creating Prometheus configuration..."
cat > infrastructure/monitoring/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'backend'
    static_configs:
      - targets: ['host.docker.internal:8080']
    metrics_path: '/actuator/prometheus'

  - job_name: 'ml-service'
    static_configs:
      - targets: ['host.docker.internal:8000']
    metrics_path: '/metrics'

  - job_name: 'postgres-exporter'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'redis-exporter'
    static_configs:
      - targets: ['redis-exporter:9121']
EOF

# Install Node.js dependencies if package.json exists
if [ -f "package.json" ]; then
    print_status "Installing Node.js dependencies..."
    npm install
fi

# Install Husky hooks
if [ -d "node_modules" ]; then
    print_status "Setting up Git hooks with Husky..."
    npx husky install
fi

# Start infrastructure services
print_status "Starting infrastructure services..."
docker-compose up -d

# Wait for services to be healthy
print_status "Waiting for services to be ready..."
sleep 30

# Check service health
services=("postgres" "redis" "elasticsearch" "keycloak")
for service in "${services[@]}"; do
    if docker-compose ps | grep -q "$service.*healthy"; then
        print_status "$service is healthy ✅"
    else
        print_warning "$service is not healthy yet ⚠️"
    fi
done

print_status "Development environment setup complete! 🎉"
print_status ""
print_status "Services available at:"
print_status "  • PostgreSQL: localhost:5432"
print_status "  • Redis: localhost:6379"
print_status "  • Elasticsearch: http://localhost:9200"
print_status "  • Keycloak: http://localhost:8080"
print_status "  • Prometheus: http://localhost:9090"
print_status "  • Grafana: http://localhost:3001 (admin/admin123)"
print_status "  • Adminer: http://localhost:8081"
print_status ""
print_status "Next steps:"
print_status "  1. Set up backend: cd apps/backend && ./mvnw spring-boot:run"
print_status "  2. Set up frontend: cd apps/frontend && npm run dev"
print_status "  3. Set up ML service: cd apps/ml-service && poetry run uvicorn main:app --reload"
print_status ""
print_status "Happy coding! 🚀"