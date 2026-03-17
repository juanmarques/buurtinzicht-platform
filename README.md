# Buurtinzicht - Neighborhood Insights Platform

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Belgian neighborhood analysis platform that aggregates government data sources, real estate pricing, and spatial analytics to generate quality-of-life scorecards for any Belgian neighborhood.

## Features

### Neighborhood Scorecards
Multi-dimensional scoring across 5 weighted categories (15 metrics total):

| Category | Weight | Data Sources |
|----------|--------|-------------|
| Infrastructure | 25% | REIP (building permits, flood zones, energy labels) |
| Economic | 25% | Immoweb (property prices), Statbel (income, employment) |
| Social/Cultural | 25% | Statbel (demographics, education) |
| Environmental | 15% | VMM (air/water quality, flood risk) |
| Safety | 10% | Statbel (crime statistics) |

### Implemented Features
- **Scorecard Generation** -- Weighted multi-metric scoring with Redis caching
- **Neighborhood Comparison** -- Side-by-side analysis with difference calculation
- **Spatial Queries** -- PostGIS-backed radius search, boundary lookup (GeoJSON), adjacent area detection
- **Real Estate Data** -- Immoweb property prices via Firecrawl web scraping + Gemini AI parsing
- **Subscription System** -- Stripe-powered freemium model (Free / Pro at 9.99/mo / Premium at 19.99/mo)
- **User Authentication** -- JWT with SHA-256 password hashing, account lockout (5 attempts = 15min)
- **Multi-language UI** -- Dutch, French, English, German (next-intl)
- **Interactive Maps** -- Leaflet + OpenStreetMap with neighborhood boundaries
- **Scorecard Sharing** -- Shareable URLs with TTL expiration
- **Favorites** -- Save and track neighborhoods
- **Address Geocoding** -- Belgian postal code to NIS code conversion

## Architecture

### Tech Stack

| Component | Technology |
|-----------|-----------|
| Frontend | Next.js 15 + React 19, Tailwind CSS, Leaflet maps |
| Backend | Spring Boot 3.5 (Java 21), Spring Security + JWT |
| Database | PostgreSQL 16 + PostGIS 3.4 (spatial queries) |
| Cache | Redis 7.2 (scorecard caching, TTL: 10min-2hr) |
| Payments | Stripe (subscriptions, webhooks, customer portal) |
| Scraping | Firecrawl API (Immoweb) + Google Gemini (text parsing) |
| Migrations | Flyway (9 versioned migrations) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

### Project Structure

```
apps/
  frontend/          # Next.js 15 (React 19, Tailwind, Leaflet)
  backend/           # Spring Boot 3.5 (Java 21)
    web/controller/  # 11 REST controllers
    scorecard/       # Scoring engine (15 metrics, 5 categories)
    spatial/         # PostGIS spatial queries
    payment/         # Stripe subscription management
    integration/     # Firecrawl, Statbel, Immoweb, Gemini
    security/        # JWT auth, password hashing
    domain/          # JPA entities, repositories
infrastructure/      # Docker, Kubernetes, Helm, Terraform
scripts/             # Data population, database setup
```

### Key API Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /api/scorecards/{nisCode}` | Generate neighborhood scorecard |
| `GET /api/scorecards/compare` | Side-by-side comparison |
| `POST /api/spatial/neighborhoods/search` | Radius-based search |
| `GET /api/spatial/neighborhoods/{nisCode}/boundary` | GeoJSON boundary |
| `POST /api/auth/register` | User registration |
| `POST /api/auth/login` | JWT authentication |
| `POST /api/payments/subscribe` | Create Stripe subscription |
| `GET /api/payments/plans` | Available subscription tiers |

## Development Setup

```bash
# Start infrastructure (PostgreSQL + Redis)
docker-compose up -d

# Backend (port 8450)
cd apps/backend
mvn spring-boot:run
# Swagger UI: http://localhost:8450/api/swagger-ui.html

# Frontend (port 3000)
cd apps/frontend
npm install && npm run dev
```

### Environment Variables

| Variable | Purpose |
|----------|---------|
| `STRIPE_SECRET_KEY` | Stripe payment processing |
| `FIRECRAWL_API_KEY` | Immoweb web scraping |
| `GEMINI_API_KEY` | AI text parsing for scraped data |
| `JWT_SECRET` | Authentication token signing |

### Database

PostgreSQL 16 with PostGIS extension. Flyway manages 9 migrations covering: neighborhoods, scorecards, payments, authentication, property prices, scorecard shares, and user history.

```
Port: 5433 (dev) / 5432 (prod)
Database: buurtinzicht
```

## Subscription Tiers

| Tier | Price | Searches | Scorecards | Features |
|------|-------|----------|------------|----------|
| Free | 0 | 3/month | 10/month | Basic scorecard |
| Pro | 9.99/mo | Unlimited | 25/month | Favorites, alerts |
| Premium | 19.99/mo | Unlimited | Unlimited | API access, PDF reports |

## Data Sources

- **Statbel** -- Federal statistics (demographics, crime, income, education)
- **REIP** -- Building permits, flood zones, energy labels
- **VMM** -- Environmental data (air/water quality)
- **IRISnet** -- Brussels-specific data (traffic, noise, green spaces)
- **Immoweb** -- Property prices (scraped via Firecrawl, parsed via Gemini)

## License

This project is licensed under the MIT License -- see the [LICENSE](LICENSE) file for details.
