# API Specification Document

# Seat Management System

Version: 1.0

Status: Approved

API Style: REST

Protocol: HTTPS

Payload Format: JSON

---

# 1. API Standards

## Base URL

```text
/api/v1
```

---

## Content Type

Request:

```http
Content-Type: application/json
```

Response:

```http
Content-Type: application/json
```

---

## Authentication

All APIs require JWT authentication except:

```text
/auth/microsoft/login

/health
```

---

## Authorization

RBAC enforced on every protected endpoint.

Roles:

```text
SUPER_ADMIN

HR_ADMIN

MANAGER

EMPLOYEE
```

---

# 2. Standard Response Contract

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
  "errors": [
    {
      "field": "employeeId",
      "message": "Employee not found"
    }
  ]
}
```

---

# 3. Pagination Standard

## Request

```http
?page=0&size=20&sort=createdAt,desc
```

---

## Response

```json
{
  "success": true,
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

# 4. Authentication APIs

## Login

### Endpoint

```http
GET /auth/microsoft/login
```

### Purpose

Redirect user to Microsoft OAuth login.

---

## Current User

### Endpoint

```http
GET /auth/me
```

### Response

```json
{
  "employeeId": "EMP001",
  "name": "Bhagya",
  "email": "user@company.com",
  "roles": ["HR_ADMIN"]
}
```

---

## Logout

### Endpoint

```http
POST /auth/logout
```

### Response

```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

# 5. Employee APIs

## Get Employees

### Endpoint

```http
GET /employees
```

### Permissions

```text
HR_ADMIN

SUPER_ADMIN
```

---

## Search Employees

### Endpoint

```http
GET /employees/search
```

### Query Params

```http
?q=bhagya
```

---

### Response

```json
[
  {
    "employeeId": "EMP001",
    "name": "Bhagya",
    "email": "user@company.com"
  }
]
```

---

## Get Employee Details

### Endpoint

```http
GET /employees/{employeeId}
```

---

## Sync Employees

### Endpoint

```http
POST /employees/sync
```

### Permissions

```text
SUPER_ADMIN

HR_ADMIN
```

---

# 6. Branch APIs

## Create Branch

### Endpoint

```http
POST /branches
```

### Request

```json
{
  "branchCode": "SJR",
  "branchName": "SJR Campus"
}
```

---

## Get Branches

### Endpoint

```http
GET /branches
```

---

## Update Branch

### Endpoint

```http
PUT /branches/{branchId}
```

---

## Delete Branch

### Endpoint

```http
DELETE /branches/{branchId}
```

---

# 7. Floor APIs

## Create Floor

```http
POST /floors
```

---

## Get Floors

```http
GET /floors
```

---

## Update Floor

```http
PUT /floors/{floorId}
```

---

## Get Floor Details

```http
GET /floors/{floorId}
```

---

# 8. Layout APIs

## Upload Layout

### Endpoint

```http
POST /layouts/upload
```

### Request

```multipart
file
floorId
```

### Response

```json
{
  "layoutVersionId": "LAYOUT001",
  "status": "DRAFT"
}
```

---

## Validate Layout

### Endpoint

```http
POST /layouts/validate
```

### Request

```json
{
  "layoutVersionId": "LAYOUT001"
}
```

---

## Migration Preview

### Endpoint

```http
POST /layouts/migration-preview
```

### Request

```json
{
  "layoutVersionId": "LAYOUT001"
}
```

### Response

```json
{
  "matchedSeats": 200,
  "removedSeats": 10,
  "newSeats": 15
}
```

---

## Activate Layout

### Endpoint

```http
POST /layouts/{layoutVersionId}/activate
```

---

## Get Active Layout

### Endpoint

```http
GET /layouts/floor/{floorId}
```

---

# 9. Seat APIs

## Get Seat Details

### Endpoint

```http
GET /seats/{seatCode}/details
```

### Response

```json
{
  "seatCode": "A101",
  "employeeId": "EMP001",
  "employeeName": "Bhagya"
}
```

---

## Assign Seat

### Endpoint

```http
POST /seats/assign
```

### Request

```json
{
  "seatCode": "A101",
  "employeeId": "EMP001"
}
```

---

## Reassign Seat

### Endpoint

```http
PUT /seats/reassign
```

### Request

```json
{
  "oldSeatCode": "A101",
  "newSeatCode": "A102",
  "employeeId": "EMP001"
}
```

---

## Temporary Assignment

### Endpoint

```http
POST /seats/temporary-assign
```

### Request

```json
{
  "seatCode": "A101",
  "employeeId": "EMP001",
  "startDate": "2026-01-01",
  "endDate": "2026-01-10"
}
```

---

## Unassign Seat

### Endpoint

```http
POST /seats/unassign
```

### Request

```json
{
  "seatCode": "A101"
}
```

---

# 10. Meeting Room APIs

## Create Booking

### Endpoint

```http
POST /bookings
```

### Request

```json
{
  "roomId": "ROOM001",
  "startTime": "2026-01-01T10:00:00",
  "endTime": "2026-01-01T11:00:00"
}
```

---

## Update Booking

### Endpoint

```http
PUT /bookings/{bookingId}
```

---

## Cancel Booking

### Endpoint

```http
DELETE /bookings/{bookingId}
```

---

## Available Rooms

### Endpoint

```http
GET /bookings/available
```

### Query Params

```http
startTime

endTime
```

---

# 11. Analytics APIs

## Occupancy Analytics

### Endpoint

```http
GET /analytics/occupancy
```

---

## Utilization Analytics

### Endpoint

```http
GET /analytics/utilization
```

---

## Export Analytics

### Endpoint

```http
POST /analytics/export
```

---

# 12. Audit APIs

## Get Audit Logs

### Endpoint

```http
GET /audit/logs
```

---

## Get Audit Details

### Endpoint

```http
GET /audit/logs/{auditId}
```

---

# 13. Search APIs

## Employee Search

### Endpoint

```http
GET /search/employees
```

### Query Params

```http
q
```

---

## Seat Search

### Endpoint

```http
GET /search/seats
```

### Query Params

```http
q
```

---

# 14. Notification APIs

## Get Notifications

### Endpoint

```http
GET /notifications
```

---

## Mark As Read

### Endpoint

```http
PUT /notifications/{notificationId}/read
```

---

# 15. WebSocket API

## Endpoint

```text
/ws
```

---

## Subscription Channel

```text
/ws/floor/{floorId}
```

---

## Event Structure

```json
{
  "eventType": "seat.assigned",
  "entityId": "A101",
  "timestamp": "2026-01-01T10:00:00"
}
```

---

## Supported Events

### Seat Events

```text
seat.assigned

seat.unassigned

seat.updated
```

### Layout Events

```text
layout.activated

layout.updated
```

### Booking Events

```text
booking.created

booking.cancelled
```

---

# 16. Validation Rules

## Seat Assignment

Validation:

```text
Employee Exists

Employee Active

Seat Exists

Seat Available
```

---

## Temporary Assignment

Validation:

```text
Start Date Required

End Date Required

Start < End
```

---

## Booking

Validation:

```text
Room Exists

Room Available

No Overlapping Booking

Valid Duration
```

---

## Layout Activation

Validation:

```text
Layout Exists

Validation Passed

No Duplicate Seats

No Geometry Errors
```

---

# 17. Error Codes

| Code       | Description            |
| ---------- | ---------------------- |
| AUTH_001   | Unauthorized           |
| AUTH_002   | Invalid Token          |
| EMP_001    | Employee Not Found     |
| SEAT_001   | Seat Not Found         |
| SEAT_002   | Seat Already Assigned  |
| ROOM_001   | Room Not Found         |
| ROOM_002   | Booking Conflict       |
| LAYOUT_001 | Layout Not Found       |
| LAYOUT_002 | Validation Failed      |
| AUDIT_001  | Audit Record Not Found |
| SYS_001    | Internal Server Error  |

---

# 18. Rate Limiting

Protected APIs:

```text
100 Requests Per Minute
```

Search APIs:

```text
300 Requests Per Minute
```

---

# 19. API Versioning

Current Version:

```text
v1
```

Future Versions:

```text
/ api / v2

/ api / v3
```

Older versions remain backward compatible until officially deprecated.

---

# 20. Approval

Product Owner: __________

Architecture Lead: __________

Engineering Lead: __________

QA Lead: __________

Approval Date: __________