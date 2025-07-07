# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FastTicketing is a high-performance seat-based ticket booking system built with microservices architecture, designed to handle massive concurrent traffic for performance events. The system implements distributed locking, queue management, and Redis caching for reliable booking operations under heavy load.

## Architecture

### Microservices Structure
- **`common`** - Shared configurations, utilities, exceptions, and cross-cutting concerns
- **`user-service`** - User management and authentication
- **`performance-service`** - Venue, hall, performance scheduling, and seat management
- **`ticket-service`** - Core ticketing logic, reservations, queue management, and booking flow
- **`app`** - Main application entry point that aggregates all services
- **`frontend`** - React/TypeScript SPA with admin and user interfaces

### Domain Architecture
Each service follows **Hexagonal Architecture** with clear separation:
- `adapter/in` - Controllers and request/response objects
- `adapter/out` - Repositories and external integrations
- `application` - Service layer with business logic and DTOs
- `domain` - Entities, enums, and core domain logic

## Technology Stack

**Backend**: Java 17, Spring Boot 3.4.2, Spring Data JPA, QueryDSL, MySQL 8.0, Redis, Redisson
**Frontend**: React 19, TypeScript 5.8, Vite 6.3, React Router DOM 7.6
**Build**: Gradle 8.12.1 (backend), npm (frontend)

## Development Commands

### Backend Commands
```bash
# Build entire project
./gradlew build

# Run individual services (for development)
./gradlew :ticket-service:bootRun
./gradlew :performance-service:bootRun  
./gradlew :user-service:bootRun

# Run main application (production-like)
./gradlew :app:bootRun

# Run tests
./gradlew test
./gradlew :ticket-service:test
./gradlew :performance-service:test
```

### Frontend Commands
```bash
cd frontend
npm install     # Install dependencies
npm run dev     # Development server (proxy to https://localhost:3573)
npm run build   # Production build
npm run lint    # ESLint code quality check
```

## Core Business Logic

### Ticket Booking Flow
1. **Queue Management**: Redis SortedSet-based queue system for high-traffic scenarios
2. **Seat Reservation**: State machine `AVAILABLE → RESERVED → CANCEL_PENDING → AVAILABLE`
3. **Distributed Locking**: Redisson locks prevent race conditions on seat booking
4. **TTL Expiry**: 10-minute payment window with automatic cleanup via schedulers

### Key Concurrency Patterns
- **Optimistic locking** in JPA entities with `@Version`
- **Distributed locks** using Redisson for critical sections
- **Redis caching** for seat availability with cache invalidation
- **Scheduled tasks** for reservation expiry and cleanup

### Database Design
- **Venue → Hall → Seat** hierarchy for venue management
- **Performance → PerformanceSchedule** for event scheduling  
- **TicketOpen** entities control booking time windows
- **Reservation** system with status tracking and expiry

## Configuration Notes

- **Server**: Runs on port 3573 with SSL enabled (custom keystore)
- **Database**: MySQL `springpay` schema with HikariCP connection pooling (5-20 connections)
- **Redis**: localhost:6379 for caching and distributed locking
- **Frontend Proxy**: `/api` routes proxy to `https://localhost:3573`

## Testing Approach

- **Unit tests** focus on service layer business logic
- **Concurrency tests** validate race condition handling (see `ReservationServiceConcurrencyTest`)
- **Integration tests** cover full API request/response flows
- Use `@Transactional` and `@DataJpaTest` for database testing

## Performance Considerations

The system is optimized for high-traffic scenarios:
- **Bulk operations** for seat generation (`SeatsGenerateService`)
- **Connection pooling** and query optimization with QueryDSL
- **Redis pipeline operations** for batch cache updates
- **Stateless service design** for horizontal scaling
- **Database read replicas** support (configured in `JpaConfig`)

## Development Guidelines

- Use **Request/Response objects** only in controller layer
- **DTO pattern** for service-to-service communication
- **Entity validation** at domain level with appropriate constraints
- **Error handling** via `GlobalExceptionHandler` with custom error codes
- Follow existing **naming conventions** for consistent code style