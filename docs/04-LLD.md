# Low Level Design (LLD)

# Seat Management System

Version: 1.0

Status: Approved

---

# 1. Introduction

## Purpose

This document describes the detailed technical design of the Seat Management System.

The objective is to provide implementation-level guidance for:

* Backend Developers
* Frontend Developers
* QA Team
* DevOps Team

This document defines:

* Module structure
* Package structure
* Entity design
* DTO design
* Repository design
* Service design
* API flow
* Validation rules
* Audit requirements
* WebSocket implementation

---

# 2. Backend Architecture

## Base Package

```text
com.iexceed.seatmanagement
```

---

## Module Structure

```text
auth
employees
roles
teams
branches
floors
layouts
parser
seats
meetings
analytics
audit
search
notifications
websocket
common
```

---

# 3. Standard Module Structure

Each module shall follow:

```text
module
│
├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
├── validator
├── exception
└── util
```

Example:

```text
employees
│
├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
└── validator
```

---

# 4. Common Package Structure

```text
common
│
├── config
├── constants
├── enums
├── exception
├── response
├── security
├── utility
├── validation
└── audit
```

---

# 5. Authentication Module

## Responsibilities

* Microsoft OAuth Login
* JWT Creation
* JWT Validation
* User Context Loading

---

## Classes

```text
AuthController

AuthService

JwtService

MicrosoftOAuthService

UserContextService
```

---

## APIs

```http
GET /auth/microsoft/login

GET /auth/me

POST /auth/logout
```

---

# 6. Employee Module

## Entity

Employee

```java
String id;
String employeeId;
String firstName;
String lastName;
String email;
String designation;
String department;
String status;
String branchId;
```

---

## DTOs

### EmployeeResponseDTO

```java
String employeeId;
String name;
String email;
String designation;
```

### EmployeeSearchDTO

```java
String employeeId;
String employeeName;
String teamName;
```

---

## Repository

```java
EmployeeRepository
```

Methods:

```java
findByEmployeeId()

findByEmail()

searchEmployees()

findActiveEmployees()
```

---

## Service

```java
EmployeeService
```

Methods:

```java
syncEmployees()

getEmployee()

searchEmployees()

updateEmployee()
```

---

# 7. Branch Module

## Entity

Branch

```java
String id;
String branchCode;
String branchName;
Boolean active;
```

---

## Service Methods

```java
createBranch()

updateBranch()

getBranches()

deactivateBranch()
```

---

# 8. Floor Module

## Entity

Floor

```java
String id;
String floorCode;
String floorName;
String branchId;
Boolean active;
```

---

## Service Methods

```java
createFloor()

updateFloor()

getFloors()

getActiveLayout()
```

---

# 9. Layout Module

## Entity

LayoutVersion

```java
String id;
String floorId;
String versionNumber;
String status;
String uploadedBy;
Date uploadedAt;
Boolean active;
```

---

## Status Values

```text
DRAFT

VALIDATING

READY_FOR_REVIEW

FAILED

ACTIVATED

ARCHIVED
```

---

## APIs

```http
POST /layouts/upload

POST /layouts/validate

POST /layouts/migration-preview

POST /layouts/{id}/activate

GET /layouts/floor/{floorId}
```

---

## Service Methods

```java
uploadLayout()

validateLayout()

migrationPreview()

activateLayout()

getActiveLayout()
```

---

# 10. Parser Module

## Responsibilities

* Read Excel
* Detect Merged Cells
* Generate Scene Graph
* Validate Layout

---

## Pipeline

```text
Workbook Loader
      ↓
Sheet Scanner
      ↓
Merge Detector
      ↓
Cell Classifier
      ↓
Coordinate Generator
      ↓
Scene Graph Builder
      ↓
Validation Engine
```

---

## Core Classes

```java
WorkbookLoader

SheetScanner

MergeDetector

CellClassifier

CoordinateGenerator

SceneGraphBuilder

LayoutValidator
```

---

# 11. Layout Element Design

## LayoutElement

```java
String id;

String layoutVersionId;

String elementCode;

String elementType;

Integer x;

Integer y;

Integer width;

Integer height;

String status;
```

---

## Element Types

```text
SEAT

ROOM

PANTRY

UTILITY

LABEL
```

---

# 12. Seat Module

## Seat Assignment Entity

```java
String id;

String seatCode;

String employeeId;

Date effectiveFrom;

Date effectiveTo;

String status;
```

---

## Status Values

```text
ACTIVE

INACTIVE

EXPIRED
```

---

## APIs

```http
GET /seats/{seatId}/details

POST /seats/assign

PUT /seats/reassign

POST /seats/temporary-assign

POST /seats/unassign
```

---

## Service Methods

```java
assignSeat()

reassignSeat()

temporaryAssign()

unassignSeat()

getSeatDetails()
```

---

## Validation Rules

### Assign Seat

Validate:

```text
Employee Exists

Employee Active

Seat Exists

Seat Available

No Existing Assignment
```

---

# 13. Meeting Module

## Meeting Room Entity

```java
String id;

String roomCode;

String roomName;

Integer capacity;

String status;
```

---

## Booking Entity

```java
String id;

String roomId;

String bookedBy;

Date startTime;

Date endTime;
```

---

## APIs

```http
POST /bookings

PUT /bookings/{id}

DELETE /bookings/{id}

GET /bookings/available
```

---

## Service Methods

```java
createBooking()

updateBooking()

cancelBooking()

searchAvailability()
```

---

## Validation Rules

```text
No Overlapping Bookings

Room Available

Valid Duration

Authorized User
```

---

# 14. Analytics Module

## Responsibilities

* Occupancy Analytics
* Utilization Analytics
* Reports

---

## APIs

```http
GET /analytics/occupancy

GET /analytics/utilization

POST /analytics/export
```

---

## Service Methods

```java
getOccupancy()

getUtilization()

exportReport()
```

---

# 15. Audit Module

## Entity

AuditLog

```java
String id;

String entityType;

String entityId;

String action;

String userId;

Object beforeState;

Object afterState;

Date timestamp;
```

---

## Service Methods

```java
createAuditLog()

getAuditHistory()

exportAuditLogs()
```

---

## Mandatory Audit Events

```text
Seat Assignment

Seat Reassignment

Seat Unassignment

Layout Upload

Layout Activation

Booking Creation

Booking Cancellation

Role Update
```

---

# 16. Search Module

## Search Types

### Employee Search

Fields:

```text
Employee ID

Employee Name

Email

Team
```

---

### Seat Search

Fields:

```text
Seat Code

Employee Name

Employee ID
```

---

## APIs

```http
GET /search/employees

GET /search/seats
```

---

# 17. Notification Module

## Responsibilities

```text
In-App Notifications

System Alerts

Layout Notifications
```

---

## Notification Types

```text
INFO

WARNING

ERROR

SUCCESS
```

---

# 18. WebSocket Module

## Endpoint

```text
/ws
```

---

## Topics

```text
/ws/floor/{floorId}
```

---

## Event Structure

```json
{
  "eventType": "seat.updated",
  "entityId": "A101",
  "timestamp": "2026-01-01T10:00:00"
}
```

---

## Supported Events

```text
seat.assigned

seat.updated

seat.unassigned

layout.activated

booking.created

booking.cancelled
```

---

# 19. Standard API Response

## Success Response

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {}
}
```

---

## Error Response

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": []
}
```

---

# 20. Exception Handling

## Global Exception Handler

```java
GlobalExceptionHandler
```

Handles:

```text
ValidationException

BusinessException

ResourceNotFoundException

UnauthorizedException

InternalServerException
```

---

# 21. Validation Framework

## Request Validation

Use:

```java
@Valid
@NotNull
@NotBlank
@Size
@Pattern
```

---

## Business Validation

Performed inside service layer.

Examples:

```text
Seat Availability

Duplicate Seat

Booking Conflict

Layout Activation Rules
```

---

# 22. Transaction Management

Transactional Operations:

```text
Seat Assignment

Seat Reassignment

Layout Activation

Booking Creation

Booking Cancellation
```

Implementation:

```java
@Transactional
```

---

# 23. Logging Strategy

Log Levels:

```text
INFO

WARN

ERROR

DEBUG
```

Capture:

```text
API Requests

Business Operations

Exceptions

Security Events
```

---

# 24. Testing Requirements

## Unit Testing

Coverage Target:

```text
80%+
```

---

## Integration Testing

Modules:

```text
Authentication

Employees

Layouts

Seats

Meetings
```

---

## API Testing

Tools:

```text
Postman

Rest Assured
```

---

# 25. Approval

Product Owner: __________

Architecture Lead: __________

Engineering Lead: __________

QA Lead: __________

Approval Date: __________