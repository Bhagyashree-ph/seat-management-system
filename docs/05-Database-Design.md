# Database Design Document

# Seat Management System

Version: 1.0

Status: Approved

Database: MongoDB

Cache: Redis

---

# 1. Introduction

## Purpose

This document defines the database architecture, collection design, indexing strategy, relationships, audit strategy, and data retention policies for the Seat Management System.

The database design supports:

* Multi-branch operations
* Multi-floor layouts
* Layout versioning
* Seat assignment history
* Meeting room bookings
* Auditability
* Analytics

---

# 2. Database Architecture

## Primary Database

MongoDB

Reason:

* Flexible schema
* Hierarchical document support
* Fast development
* Good scalability
* Suitable for layout metadata

---

## Cache Layer

Redis

Purpose:

* Active layouts
* Search suggestions
* Analytics snapshots
* Role permissions

---

# 3. Collection Overview

```text
employees

employee_roles

teams

employee_teams

team_managers

branches

floors

layout_versions

layout_elements

seat_assignments

meeting_rooms

meeting_bookings

meeting_attendees

audit_logs

analytics_snapshots

websocket_events

notifications
```

---

# 4. Common Fields

Every collection shall contain:

```json
{
  "_id": "ObjectId",
  "createdAt": "Date",
  "createdBy": "String",
  "updatedAt": "Date",
  "updatedBy": "String",
  "isDeleted": false
}
```

---

# 5. Employees Collection

## Collection

```text
employees
```

---

## Document

```json
{
  "_id": {
    "$oid": "6a21284905716fc65605db38"
  },
  "employeeCode": "4905",
  "zohoEmployeeId": "341317000088044037",
  "zuid": "917823968",
  "firstName": "Bhagyashree",
  "lastName": "LNU",
  "displayName": "Bhagyashree LNU",
  "nickName": "",
  "email": "bhagyashree.lnu@i-exceed.com",
  "mobile": "91-9483451311",
  "workPhone": "",
  "extension": "",
  "designation": "Software Engineer",
  "designationId": "341317000000104947",
  "department": "Release Management",
  "departmentId": "341317000079177842",
  "grade": "A2",
  "gradeId": "341317000017177335",
  "workLocation": "Bangalore",
  "officeLocation": "Bangalore",
  "seatingLocation": "SJR - First floor",
  "seatingLocationId": "341317000086100744",
  "reportingManagerName": "Kommaraju Venkateswara - Rao",
  "reportingManagerZohoId": "341317000071712447",
  "reportingManagerEmail": "venkateswara.rao1@i-exceed.com",
  "secondReportingManagerName": "Joyson - C J",
  "secondReportingManagerZohoId": "341317000072205079",
  "employmentStatus": "Active",
  "employmentStatusType": 1,
  "primarySkills": [
    "Java",
    "Spring Boot",
    "Microservices",
    "Rest API",
    "MongoDB"
  ],
  "secondarySkills": [
    "Javascript",
    "React JS",
    "RabbitMQ"
  ],
  "expertise": "Java (Core Java, Java 8 features, Streams)\nSpring Boot & REST API Development\nMicroservices Basics\nSQL & Database Queries\nDebugging and Code Optimization\nGit / Version Control",
  "aboutMe": "Software Engineer with around 2 years of experience in building backend applications using Java and Spring Boot. Skilled in developing REST APIs, working with relational and non-relational databases, and writing clean, maintainable code. Passionate about learning new technologies and solving complex problems efficiently. Experienced in collaborating within agile teams to deliver scalable solutions.",
  "profilePhotoUrl": "https://people.zoho.com/api/viewEmployeePhoto?filename=1027689000001888105",
  "active": true,
  "deleted": false,
  "source": "ZOHO",
  "zohoCreatedTime": {
    "$date": "2026-03-13T06:23:24.828Z"
  },
  "zohoModifiedTime": {
    "$date": "2026-05-31T15:31:38.246Z"
  },
  "lastSyncedAt": {
    "$date": "2026-06-04T10:36:57.384Z"
  },
  "_class": "com.iexceed.seatmanagement.employees.entity.Employee"
}
```

---

## Indexes

```text
employeeId (Unique)

email (Unique)

status

branchId
```

---

# 6. Roles Collection

## Collection

```text
employee_roles
```

---

## Document

```json
{
  "_id": "ObjectId",
  "employeeId": "EMP001",
  "roleCode": "HR_ADMIN"
}
```

---

## Indexes

```text
employeeId

roleCode
```

---

# 7. Teams Collection

## Collection

```text
teams
```

---

## Document

```json
{
  "_id": "ObjectId",
  "teamCode": "ENG",
  "teamName": "Engineering"
}
```

---

# 8. Branches Collection

## Collection

```text
branches
```

---

## Document

```json
{
  "_id": "ObjectId",
  "branchCode": "SJR",
  "branchName": "SJR Campus",
  "city":"Bangalore",
  "address":"kormanangala 2nd stage",
  "timezone":"Asia/Kolata",
  "status": "ACTIVE"
}
```

---

## Indexes

```text
branchCode (Unique)
```

---

# 9. Floors Collection

## Collection

```text
floors
```

---

## Document

```json
{
  "_id": "ObjectId",
  "branchId": "ObjectId",
  "floorCode": "F1",
  "floorName": "Floor 1",
  "status": "ACTIVE",
  "totalSeats":10,
  "layoutEnabled":true,
  "activeLayoutVersionId":"verion1"
}
```

---

## Indexes

```text
branchId

floorCode
```

---

# 10. Layout Versions Collection

## Collection

```text
layout_versions
```

---

## Purpose

Stores layout metadata.

No geometry stored here.

---

## Document

```json
{
  "_id": "ObjectId",
  "branchId":"ObjectId",
  "floorId": "ObjectId",
  "versionNumber": 5,
  "status": "ACTIVATED",
  "uploadedBy": "EMP001",
  "uploadedAt": "Date",
  "activatedAt": "Date",
  "isActive": true
}
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

## Indexes

```text
floorId

status

isActive
```

---

# 11. Layout Elements Collection

## Collection

```text
layout_elements
```

---

## Purpose

Stores seats, rooms, labels and utilities.

---

## Document

```json
{
  "_id": "ObjectId",
  "layoutVersionId": "ObjectId",
  "elementType": "SEAT",
  "elementCode": "A101",
  "x": 10,
  "y": 5,
  "width": 1,
  "height": 1,
  "status": "ACTIVE"
}
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

## Indexes

```text
layoutVersionId

elementType

elementCode
```

---

# 12. Seat Assignments Collection

## Collection

```text
seat_assignments
```

---

## Purpose

Stores seat assignment history.

Never overwritten.

Temporal model.

---

## Document

```json
{
  "_id": "ObjectId",
  "seatCode": "A101",
  "employeeId": "EMP001",
  "assignmentType": "PERMANENT",
  "effectiveFrom": "Date",
  "effectiveTo": null,
  "status": "ACTIVE"
}
```

---

## Assignment Types

```text
PERMANENT

TEMPORARY
```

---

## Status Values

```text
ACTIVE

INACTIVE

EXPIRED
```

---

## Indexes

```text
seatCode

employeeId

status

effectiveFrom
```

---

# 13. Meeting Rooms Collection

## Collection

```text
meeting_rooms
```

---

## Document

```json
{
  "_id": "ObjectId",
  "roomCode": "CR-01",
  "roomName": "Conference Room 01",
  "capacity": 10,
  "status": "AVAILABLE"
}
```

---

## Indexes

```text
roomCode (Unique)
```

---

# 14. Meeting Bookings Collection

## Collection

```text
meeting_bookings
```

---

## Document

```json
{
  "_id": "ObjectId",
  "roomId": "ObjectId",
  "bookedBy": "EMP001",
  "startTime": "Date",
  "endTime": "Date",
  "status": "ACTIVE"
}
```

---

## Status Values

```text
ACTIVE

CANCELLED

COMPLETED
```

---

## Indexes

```text
roomId

startTime

endTime

status
```

---

# 15. Audit Logs Collection

## Collection

```text
audit_logs
```

---

## Purpose

Immutable audit records.

Never modified.

Never deleted.

---

## Document

```json
{
  "_id": "ObjectId",
  "entityType": "SEAT_ASSIGNMENT",
  "entityId": "123",
  "action": "ASSIGNED",
  "userId": "EMP001",
  "beforeState": {},
  "afterState": {},
  "timestamp": "Date"
}
```

---

## Indexes

```text
entityType

entityId

userId

timestamp
```

---

# 16. Analytics Snapshots Collection

## Collection

```text
analytics_snapshots
```

---

## Purpose

Stores pre-calculated analytics.

---

## Document

```json
{
  "_id": "ObjectId",
  "snapshotType": "DAILY",
  "branchId": "ObjectId",
  "occupancyPercentage": 78,
  "generatedAt": "Date"
}
```

---

## Snapshot Types

```text
HOURLY

DAILY
```

---

# 17. WebSocket Events Collection

## Collection

```text
websocket_events
```

---

## Purpose

Reconnect recovery.

Event replay.

---

## Document

```json
{
  "_id": "ObjectId",
  "eventType": "seat.updated",
  "entityId": "A101",
  "version": 1001,
  "timestamp": "Date"
}
```

---

# 18. Notification Collection

## Collection

```text
notifications
```

---

## Document

```json
{
  "_id": "ObjectId",
  "employeeId": "EMP001",
  "title": "Seat Assigned",
  "message": "Seat A101 assigned",
  "status": "UNREAD"
}
```

---

# 19. Relationship Model

```text
Branch
  └── Floors
         └── Layout Versions
                └── Layout Elements

Employee
  └── Seat Assignments

Employee
  └── Roles

Employee
  └── Teams

Meeting Room
  └── Bookings
```

---

# 20. Soft Delete Strategy

Supported Collections:

```text
employees

branches

floors

meeting_rooms
```

Field:

```json
{
  "isDeleted": false
}
```

---

# 21. Audit Strategy

Audit Required For:

```text
Seat Assignment

Seat Reassignment

Seat Unassignment

Layout Upload

Layout Activation

Booking Creation

Booking Cancellation

Role Updates
```

---

# 22. Data Retention

| Data Type           | Retention |
| ------------------- | --------- |
| Audit Logs          | Permanent |
| Layout Versions     | Permanent |
| Assignment History  | Permanent |
| Analytics Snapshots | Permanent |
| Notifications       | 1 Year    |

---

# 23. Redis Cache Design

## Cache Keys

```text
layout:active:{floorId}

employee:{employeeId}

permissions:{employeeId}

analytics:daily:{branchId}

search:suggestions
```

---

## TTL Strategy

```text
Permissions -> 30 minutes

Analytics -> 1 hour

Search Suggestions -> 15 minutes

Layouts -> No Expiry
```

---

# 24. Database Standards

## Naming Convention

Collections:

```text
snake_case
```

Fields:

```text
camelCase
```

---

## ID Strategy

MongoDB ObjectId

Business IDs:

```text
EMP001

A101

CR-01

SJR
```

---

# 25. Approval

Product Owner: __________

Architecture Lead: __________

Engineering Lead: __________

Approval Date: __________