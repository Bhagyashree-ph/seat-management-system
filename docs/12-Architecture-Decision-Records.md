# Architecture Decision Records (ADR)

# Seat Management System

Version: 1.0

Status: Approved

---

# Purpose

This document records the key architectural decisions made during the design of the Seat Management System.

Each ADR captures:

* Context
* Problem
* Decision
* Alternatives Considered
* Consequences

This document serves as the architectural memory of the project.

---

# ADR-001

## Title

Modular Monolith Architecture

---

## Status

Accepted

---

## Context

The system contains multiple domains:

* Employees
* Layouts
* Seats
* Meetings
* Analytics
* Audit

A decision was required between:

* Modular Monolith
* Microservices

---

## Decision

Implement a Modular Monolith.

---

## Alternatives Considered

### Microservices

Pros:

* Independent deployment
* Independent scaling

Cons:

* Increased complexity
* Service discovery
* Distributed transactions
* Operational overhead

---

### Modular Monolith

Pros:

* Simpler deployment
* Easier debugging
* Faster development
* Lower operational cost

Cons:

* Single deployment unit

---

## Consequences

Chosen architecture:

```text
Spring Boot Modular Monolith
```

Future migration to microservices remains possible.

---

# ADR-002

## Title

MongoDB as Primary Database

---

## Status

Accepted

---

## Context

The application stores:

* Employees
* Layout Metadata
* Layout Elements
* Seat Assignments
* Audit Logs
* Analytics

Layout structures are semi-structured and may evolve.

---

## Decision

Use MongoDB.

---

## Alternatives Considered

### PostgreSQL

Pros:

* Strong relational model
* ACID support

Cons:

* Complex handling of layout structures

---

### MongoDB

Pros:

* Flexible schema
* Faster iteration
* Good fit for layout metadata
* Simple document model

Cons:

* Less relational enforcement

---

## Consequences

MongoDB becomes the system of record.

---

# ADR-003

## Title

Redis as Cache Layer

---

## Status

Accepted

---

## Context

Frequently accessed data includes:

* Active layouts
* Permissions
* Analytics
* Search suggestions

---

## Decision

Introduce Redis.

---

## Alternatives Considered

### Application Memory Cache

Pros:

* Simple

Cons:

* Not shared across instances

---

### Redis

Pros:

* Centralized cache
* Fast lookup
* Scalable

Cons:

* Additional infrastructure

---

## Consequences

Redis used strictly as cache.

No source-of-truth data stored.

---

# ADR-004

## Title

Microsoft OAuth Authentication

---

## Status

Accepted

---

## Context

Enterprise users already authenticate through Microsoft.

---

## Decision

Use Microsoft Entra ID (Azure AD).

---

## Alternatives Considered

### Local Authentication

Pros:

* Full control

Cons:

* Password management burden

---

### Microsoft OAuth

Pros:

* SSO
* Enterprise standard
* Centralized identity

Cons:

* Dependency on Microsoft ecosystem

---

## Consequences

Application remains passwordless.

---

# ADR-005

## Title

JWT Authentication

---

## Status

Accepted

---

## Context

Need stateless authentication.

---

## Decision

Use JWT.

---

## Alternatives Considered

### Session-Based Authentication

Pros:

* Simple

Cons:

* Server-side session storage

---

### JWT

Pros:

* Stateless
* Scalable
* Suitable for APIs

Cons:

* Token management required

---

## Consequences

Backend remains horizontally scalable.

---

# ADR-006

## Title

Role Based Access Control (RBAC)

---

## Status

Accepted

---

## Context

Different users require different permissions.

---

## Decision

Implement RBAC.

Roles:

```text
SUPER_ADMIN

HR_ADMIN

MANAGER

EMPLOYEE
```

---

## Consequences

Permission management becomes centralized.

---

# ADR-007

## Title

Excel-Based Layout Upload

---

## Status

Accepted

---

## Context

Business users already manage floor plans in Excel.

---

## Decision

Use Excel as layout source.

Supported format:

```text
.xlsx
```

---

## Alternatives Considered

### CAD Files

Pros:

* Precision

Cons:

* Business users cannot maintain

---

### SVG Upload

Pros:

* Ready for rendering

Cons:

* Difficult for business users

---

### Excel

Pros:

* Familiar
* Easy maintenance

Cons:

* Parsing complexity

---

## Consequences

Parser engine required.

---

# ADR-008

## Title

Scene Graph Layout Architecture

---

## Status

Accepted

---

## Context

Floor maps require efficient rendering.

---

## Decision

Generate scene graph from uploaded layouts.

---

## Structure

```text
ROOT

BACKGROUND_LAYER

ROOM_LAYER

SEAT_LAYER

LABEL_LAYER

INTERACTION_LAYER
```

---

## Consequences

Frontend rendering remains simple.

---

# ADR-009

## Title

SVG Rendering Strategy

---

## Status

Accepted

---

## Context

Users interact with seats and rooms.

---

## Decision

Use SVG rendering.

---

## Alternatives Considered

### Canvas

Pros:

* Better performance for massive datasets

Cons:

* Complex interactions

---

### SVG

Pros:

* Easy interaction
* Easy highlighting
* Better accessibility

Cons:

* Larger DOM

---

## Consequences

Interactive seat operations become simpler.

---

# ADR-010

## Title

Seat Code as Stable Identity

---

## Status

Accepted

---

## Context

Layouts change frequently.

Need stable assignment mapping.

---

## Decision

Seat Code is the business identifier.

Examples:

```text
A101

B205

DEV-22
```

---

## Consequences

Layout migrations become predictable.

---

# ADR-011

## Title

Temporal Seat Assignment Model

---

## Status

Accepted

---

## Context

Assignment history must never be lost.

---

## Decision

Store assignments as immutable records.

---

## Model

```text
effectiveFrom

effectiveTo

status
```

---

## Consequences

Full history available.

Audit reporting simplified.

---

# ADR-012

## Title

Layout Versioning Strategy

---

## Status

Accepted

---

## Context

Layouts evolve over time.

Historical layouts must remain available.

---

## Decision

Implement immutable layout versions.

---

## Rules

```text
One Active Version

Many Archived Versions
```

---

## Consequences

Rollback becomes possible.

Audit compliance improved.

---

# ADR-013

## Title

WebSocket Realtime Architecture

---

## Status

Accepted

---

## Context

Users need realtime seat updates.

---

## Decision

Use WebSocket communication.

---

## Event Types

```text
seat.assigned

seat.updated

layout.activated

booking.created
```

---

## Consequences

Realtime user experience.

Reduced polling.

---

# ADR-014

## Title

Floor-Level Event Channels

---

## Status

Accepted

---

## Context

Broadcasting all events to all users is inefficient.

---

## Decision

Use floor-scoped channels.

Example:

```text
/ws/floor/{floorId}
```

---

## Consequences

Reduced network traffic.

Better scalability.

---

# ADR-015

## Title

Audit-First Architecture

---

## Status

Accepted

---

## Context

Seat assignments and layout activations require traceability.

---

## Decision

All critical actions generate audit logs.

---

## Mandatory Events

```text
Seat Assignment

Seat Reassignment

Layout Activation

Booking Creation

Role Changes
```

---

## Consequences

Full compliance trail.

---

# ADR-016

## Title

Soft Delete Strategy

---

## Status

Accepted

---

## Context

Historical records must remain recoverable.

---

## Decision

Use logical deletion.

Field:

```json
{
  "isDeleted": false
}
```

---

## Consequences

Data recovery possible.

Audit integrity maintained.

---

# ADR-017

## Title

Hybrid Analytics Model

---

## Status

Accepted

---

## Context

Analytics require both realtime and historical data.

---

## Decision

Use:

```text
Live Analytics

+

Snapshot Analytics
```

---

## Consequences

Good performance.

Historical reporting support.

---

# ADR-018

## Title

Docker-Based Deployment

---

## Status

Accepted

---

## Context

Need environment consistency.

---

## Decision

Containerize all services.

---

## Containers

```text
frontend

backend

mongodb

redis

nginx
```

---

## Consequences

Simplified deployment.

Portable infrastructure.

---

# ADR-019

## Title

GitHub-Based CI/CD

---

## Status

Accepted

---

## Context

Automated deployments required.

---

## Decision

Use GitHub Actions.

---

## Consequences

Consistent release process.

Reduced manual effort.

---

# ADR-020

## Title

Desktop-First User Experience

---

## Status

Accepted

---

## Context

Primary users are HR/Admin teams using desktop systems.

---

## Decision

Optimize for desktop experience.

---

## Consequences

Improved productivity.

Mobile application deferred.

---

# Architectural Summary

Final Architecture:

```text
React Frontend
       ↓
NGINX
       ↓
Spring Boot Modular Monolith
       ↓
MongoDB + Redis
       ↓
WebSocket Event Layer
       ↓
Microsoft + Zoho Integrations
```

---

# Approval

Chief Architect: __________

Product Owner: __________

Engineering Lead: __________

Approval Date: __________