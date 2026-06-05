# Security Architecture Document

# Seat Management System

Version: 1.0

Status: Approved

Classification: Internal Enterprise Application

---

# 1. Purpose

This document defines the complete security architecture for the Seat Management System.

Objectives:

* Authentication
* Authorization
* Data Protection
* API Security
* WebSocket Security
* Audit Compliance
* Session Management
* Infrastructure Security

---

# 2. Security Principles

## SEC-001 Zero Trust

No user, device, or request is trusted by default.

Every request must be authenticated and authorized.

---

## SEC-002 Least Privilege

Users receive only the permissions required for their role.

---

## SEC-003 Defense In Depth

Security controls exist at:

* UI Layer
* API Layer
* Service Layer
* Database Layer
* Infrastructure Layer

---

## SEC-004 Auditability

All critical actions must be auditable.

---

# 3. Authentication Architecture

## Identity Provider

Microsoft Entra ID (Azure AD)

Purpose:

* Enterprise authentication
* Single Sign-On
* Centralized identity management

---

## Authentication Flow

```text
User
 ↓
React UI
 ↓
Microsoft Login
 ↓
Microsoft OAuth
 ↓
Authorization Code
 ↓
Backend
 ↓
JWT Generation
 ↓
Authenticated Session
```

---

## Supported Authentication Method

```text
OAuth2 Authorization Code Flow
```

---

## Authentication Features

* Single Sign-On
* Centralized Identity
* Passwordless Application
* Session Validation
* Automatic Token Expiry

---

# 4. JWT Architecture

## Token Type

```text
JWT Access Token
```

---

## JWT Claims

```json
{
  "employeeId": "EMP001",
  "email": "user@company.com",
  "roles": ["HR_ADMIN"],
  "branchAccess": ["SJR"],
  "floorAccess": ["F1"],
  "iat": 123456,
  "exp": 123999
}
```

---

## Token Expiry

```text
Access Token = 8 Hours
```

Configurable via application properties.

---

## JWT Validation

Validate:

* Signature
* Expiration
* Issuer
* Audience
* Employee Status

---

# 5. Authorization Architecture

## Authorization Model

Role Based Access Control (RBAC)

---

## Roles

### SUPER_ADMIN

Permissions:

* Full system access

---

### HR_ADMIN

Permissions:

* Employee Management
* Layout Management
* Seat Management
* Analytics Access

---

### MANAGER

Permissions:

* Team Visibility
* Team Seating View
* Floor Visibility

---

### EMPLOYEE

Permissions:

* Search
* Floor View
* Meeting Booking

---

# 6. Permission Matrix

| Feature            | SUPER_ADMIN | HR_ADMIN | MANAGER | EMPLOYEE |
| ------------------ | ----------- | -------- | ------- | -------- |
| View Employees     | Yes         | Yes      | Yes     | Limited  |
| Manage Employees   | Yes         | Yes      | No      | No       |
| Manage Roles       | Yes         | No       | No      | No       |
| Upload Layout      | Yes         | Yes      | No      | No       |
| Activate Layout    | Yes         | Yes      | No      | No       |
| Assign Seats       | Yes         | Yes      | No      | No       |
| Reassign Seats     | Yes         | Yes      | No      | No       |
| View Analytics     | Yes         | Yes      | Limited | No       |
| View Audit Logs    | Yes         | Yes      | No      | No       |
| Book Meeting Rooms | Yes         | Yes      | Yes     | Yes      |

---

# 7. Spring Security Architecture

## Security Filter Chain

```text
Request
 ↓
CORS Filter
 ↓
JWT Filter
 ↓
Authentication Filter
 ↓
Authorization Filter
 ↓
Controller
```

---

## Core Components

```java
SecurityConfig

JwtAuthenticationFilter

JwtService

CustomUserDetailsService

AuthenticationEntryPoint

AccessDeniedHandler
```

---

# 8. API Security

## Protected APIs

All APIs require JWT except:

```text
/auth/microsoft/login

/health
```

---

## Request Header

```http
Authorization: Bearer <token>
```

---

## Authorization Validation

Performed at:

* Controller Layer
* Service Layer

Example:

```java
@PreAuthorize("hasRole('HR_ADMIN')")
```

---

# 9. WebSocket Security

## Authentication

JWT required during connection.

---

## Validation

Validate:

* Token Signature
* Token Expiry
* User Status

---

## Subscription Authorization

User may subscribe only to authorized floors.

Example:

```text
/ws/floor/F1
```

---

# 10. Data Security

## Sensitive Data

Protected Fields:

```text
Email

Employee Details

Role Information

Audit Logs
```

---

## Data Exposure Rules

Responses expose only required fields.

No internal database fields returned.

Example:

```text
_id

createdBy

updatedBy
```

Must not be exposed.

---

# 11. Input Validation Security

## Validation Layers

### UI Validation

Basic validation.

---

### API Validation

Bean Validation.

```java
@NotNull

@NotBlank

@Size

@Pattern
```

---

### Business Validation

Service layer validation.

Examples:

* Duplicate seat
* Booking conflict
* Invalid layout

---

# 12. CORS Policy

## Allowed Origins

```text
https://seat.company.com
```

---

## Allowed Methods

```text
GET

POST

PUT

DELETE
```

---

## Credentials

Allowed.

---

# 13. Security Headers

Configured through NGINX and Spring Security.

Headers:

```text
X-Frame-Options

X-Content-Type-Options

Referrer-Policy

Content-Security-Policy

Strict-Transport-Security
```

---

# 14. Session Management

## Session Creation

Created after successful login.

---

## Session Expiry

Session expires when:

* JWT expires
* User logs out
* User disabled

---

## Forced Logout

Admin may invalidate sessions.

---

# 15. Audit Security

## Mandatory Audit Events

Audit records required for:

```text
Login

Logout

Role Changes

Seat Assignment

Seat Reassignment

Seat Unassignment

Layout Upload

Layout Activation

Booking Creation

Booking Cancellation
```

---

## Audit Record Contents

```json
{
  "userId": "EMP001",
  "action": "ASSIGN_SEAT",
  "entityType": "SEAT",
  "entityId": "A101",
  "timestamp": "2026-01-01T10:00:00"
}
```

---

# 16. MongoDB Security

## Access Model

Application-only access.

Users never connect directly.

---

## Controls

* Authentication Enabled
* Authorization Enabled
* Role-Based Database Users
* Network Restrictions

---

## Backup Security

Backups encrypted before storage.

---

# 17. Redis Security

## Usage

Caching only.

No source-of-truth data.

---

## Controls

* Password Protected
* Internal Network Only
* TLS Enabled (Production)

---

# 18. File Upload Security

## Supported Files

```text
.xlsx
```

Only.

---

## Validation

Validate:

* Extension
* MIME Type
* Size
* Structure

---

## Rejected Files

```text
.exe

.js

.bat

.sh
```

---

# 19. Rate Limiting

## API Rate Limits

Standard APIs:

```text
100 requests/minute
```

---

## Search APIs

```text
300 requests/minute
```

---

## Authentication APIs

```text
20 requests/minute
```

---

# 20. Security Monitoring

Monitor:

```text
Failed Logins

Access Denied Events

Token Validation Failures

Unexpected API Calls

WebSocket Abuse
```

---

# 21. Security Incident Response

Events:

```text
Unauthorized Access

Privilege Escalation

Data Exposure

Repeated Login Failures
```

Actions:

```text
Audit Log

Alert

Investigation

Remediation
```

---

# 22. Compliance Requirements

Requirements:

* Full Audit Trail
* Data Retention
* Access Logging
* Security Logging
* Role-Based Access Control

---

# 23. Security Risks & Mitigations

| Risk                 | Mitigation            |
| -------------------- | --------------------- |
| Unauthorized Access  | OAuth + JWT + RBAC    |
| Token Theft          | HTTPS + Expiry        |
| Privilege Escalation | Permission Validation |
| Layout Tampering     | Audit Logs            |
| Data Leakage         | Response Filtering    |
| Replay Attack        | JWT Validation        |
| API Abuse            | Rate Limiting         |

---

# 24. Future Enhancements

* MFA Enforcement
* Device Trust Validation
* Security Dashboard
* SIEM Integration
* Automated Threat Detection

---

# 25. Approval

Product Owner: __________

Security Architect: __________

Architecture Lead: __________

Engineering Lead: __________

Approval Date: __________