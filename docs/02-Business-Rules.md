# Business Rules Document

# Seat Management System

Version: 1.0

Status: Approved

---

# 1. Purpose

This document defines all business rules that govern the behavior of the Seat Management System.

These rules must be enforced consistently across:

* Backend APIs
* UI Actions
* Validation Engine
* Layout Engine
* Reporting
* Audit Logging
* Realtime Events

---

# 2. Employee Rules

## BR-EMP-001 Employee Uniqueness

Every employee must have a unique Employee ID.

Validation:

* Duplicate employee IDs are not allowed.
* Employee ID cannot be modified after creation.

---

## BR-EMP-002 Employee Status

Employee statuses:

* ACTIVE
* INACTIVE
* TERMINATED

Rules:

* Only ACTIVE employees can receive seat assignments.
* Only ACTIVE employees can book meeting rooms.

---

## BR-EMP-003 Employee Source

Employee master data originates from Zoho People.

Rules:

* Manual employee creation is not allowed.
* Employee synchronization updates existing records.
* Employee ID is the source of truth.

---

# 3. Team Rules

## BR-TEAM-001 Team Membership

An employee may belong to multiple teams.

Rules:

* Team memberships are maintained separately.
* Manager assignments are independent of team membership.

---

## BR-TEAM-002 Team Manager

A team can have multiple managers.

Rules:

* At least one active manager is recommended.
* Manager must be an ACTIVE employee.

---

# 4. Branch Rules

## BR-BRANCH-001 Branch Uniqueness

Branch code must be unique.

Examples:

SJR

SVR

HYD

---

## BR-BRANCH-002 Branch Deactivation

A branch cannot be deactivated if:

* Active floors exist.
* Active seat assignments exist.

---

# 5. Floor Rules

## BR-FLOOR-001 Floor Uniqueness

Floor code must be unique within a branch.

Example:

SJR-F1

SJR-F2

---

## BR-FLOOR-002 Active Layout

A floor can have only one active layout version.

Validation:

Maximum active layout versions = 1

---

## BR-FLOOR-003 Floor Deactivation

A floor cannot be deactivated if:

* Active layout exists.
* Active seat assignments exist.

---

# 6. Layout Rules

## BR-LAYOUT-001 Layout Versioning

Every uploaded layout creates a new version.

Rules:

* Layout versions are immutable.
* Activated layouts cannot be modified.
* Previous versions remain available.

---

## BR-LAYOUT-002 Draft State

Uploaded layouts enter DRAFT state.

Possible statuses:

* DRAFT
* VALIDATING
* READY_FOR_REVIEW
* ACTIVATED
* ARCHIVED
* FAILED

---

## BR-LAYOUT-003 Activation

Only one layout version can be active per floor.

Activation flow:

Upload
↓
Validate
↓
Review
↓
Activate

---

## BR-LAYOUT-004 Activation Validation

Layout activation is blocked when:

* Validation errors exist.
* Duplicate seat codes exist.
* Invalid geometry exists.
* Overlapping elements exist.

---

## BR-LAYOUT-005 Layout Archive

When a layout is activated:

* Previous active layout becomes ARCHIVED.
* Archived layouts remain available for audit purposes.

---

# 7. Seat Rules

## BR-SEAT-001 Seat Identity

Seat code is the permanent seat identity.

Examples:

A101

B205

DEV-22

Rules:

* Seat codes must be unique within a floor.
* Seat code changes create a new seat identity.

---

## BR-SEAT-002 Seat Types

Supported seat types:

* WORKSTATION
* CABIN
* HOT_DESK
* CONTRACTOR
* RESERVED

---

## BR-SEAT-003 Seat Status

Supported statuses:

* AVAILABLE
* OCCUPIED
* BLOCKED
* MAINTENANCE

---

## BR-SEAT-004 Blocked Seats

Blocked seats:

* Cannot be assigned.
* Cannot appear in assignment suggestions.

---

## BR-SEAT-005 Maintenance Seats

Seats under maintenance:

* Cannot receive assignments.
* Must display maintenance status.

---

# 8. Permanent Seat Assignment Rules

## BR-ASSIGN-001 Single Active Seat

An employee can have only one active permanent seat assignment.

Validation:

Maximum active assignments = 1

---

## BR-ASSIGN-002 Single Active Occupant

A seat can have only one active permanent occupant.

Validation:

Maximum active occupants = 1

---

## BR-ASSIGN-003 Assignment Eligibility

Seat assignment requires:

* Employee ACTIVE
* Seat AVAILABLE
* User has permission

---

## BR-ASSIGN-004 Reassignment

Seat reassignment automatically:

* Ends previous assignment
* Creates new assignment
* Generates audit log
* Publishes realtime event

---

## BR-ASSIGN-005 Effective Dates

Permanent assignment:

effectiveTo = NULL

Temporary assignment:

effectiveTo is mandatory

---

# 9. Temporary Assignment Rules

## BR-TEMP-001 Date Requirement

Temporary assignment requires:

* Start date
* End date

---

## BR-TEMP-002 Date Validation

Rules:

Start Date < End Date

---

## BR-TEMP-003 Expiry

Expired temporary assignments:

* Become inactive automatically.
* Generate audit event.

---

# 10. Meeting Room Rules

## BR-MEET-001 Room Identity

Meeting room code must be unique.

Examples:

CR-01

CR-02

DISCUSSION-01

---

## BR-MEET-002 Booking Conflict

Overlapping bookings are prohibited.

Validation:

No overlapping time ranges allowed.

---

## BR-MEET-003 Booking Duration

Minimum booking duration:

15 minutes

Maximum booking duration:

Configured by administrator

---

## BR-MEET-004 Room Availability

Only AVAILABLE rooms may be booked.

---

## BR-MEET-005 Booking Ownership

Only booking owner or administrator may cancel a booking.

---

# 11. Search Rules

## BR-SEARCH-001 Employee Search

Supported search fields:

* Employee ID
* Employee Name
* Email
* Team

---

## BR-SEARCH-002 Seat Search

Supported search fields:

* Seat Code
* Employee Name
* Employee ID

---

## BR-SEARCH-003 Search Visibility

Search results respect RBAC visibility rules.

Users must not see unauthorized data.

---

# 12. Analytics Rules

## BR-ANALYTICS-001 Occupancy Calculation

Occupancy Percentage:

Occupied Seats ÷ Total Available Seats × 100

---

## BR-ANALYTICS-002 Excluded Seats

Excluded from occupancy:

* BLOCKED
* MAINTENANCE

---

## BR-ANALYTICS-003 Snapshot Generation

Analytics snapshots generated:

* Hourly
* Daily

---

# 13. Audit Rules

## BR-AUDIT-001 Mandatory Logging

Audit logs required for:

* Seat Assignment
* Seat Reassignment
* Seat Unassignment
* Layout Activation
* Layout Upload
* Booking Creation
* Booking Cancellation
* Role Changes

---

## BR-AUDIT-002 Audit Immutability

Audit records cannot be modified.

Audit records cannot be deleted.

---

## BR-AUDIT-003 Audit Metadata

Audit record must capture:

* User ID
* Action
* Timestamp
* Entity Type
* Entity ID
* Before State
* After State

---

# 14. Realtime Rules

## BR-RT-001 Event Publishing

System publishes events for:

* Seat Assignment
* Seat Reassignment
* Layout Activation
* Booking Creation
* Booking Cancellation

---

## BR-RT-002 Event Ordering

Events must be processed in creation order.

---

## BR-RT-003 Reconnect Recovery

Client reconnect must support missed-event recovery.

---

# 15. Security Rules

## BR-SEC-001 Authentication

All APIs require authentication except:

* Login
* Health Check

---

## BR-SEC-002 Authorization

All protected APIs require RBAC validation.

---

## BR-SEC-003 WebSocket Security

WebSocket connections require valid JWT.

---

## BR-SEC-004 Session Expiry

Expired JWT tokens must be rejected.

---

# 16. Visibility Rules

## BR-VIS-001 Floor Visibility

Floor visibility may be:

* Public
* Restricted

---

## BR-VIS-002 Branch Visibility

Users can access only authorized branches.

---

## BR-VIS-003 Employee Visibility

Users can only view employee data permitted by their role.

---

# 17. Data Retention Rules

## BR-DATA-001 Audit Retention

Audit logs retained indefinitely.

---

## BR-DATA-002 Layout Retention

Layout versions retained indefinitely.

---

## BR-DATA-003 Assignment History

Assignment history never deleted.

---

# 18. Exception Handling Rules

## BR-EX-001 Validation Failure

Validation failures return:

* Error Code
* Error Message
* Validation Details

---

## BR-EX-002 Business Rule Violation

Business rule violations must:

* Block operation
* Generate structured response
* Preserve existing data

---

# 19. Rule Priority

Priority Order:

1. Security Rules
2. Data Integrity Rules
3. Assignment Rules
4. Layout Rules
5. Booking Rules
6. Analytics Rules

When conflicts occur, higher-priority rules take precedence.

---

# 20. Approval

Product Owner: __________

Architecture Lead: __________

Engineering Lead: __________

Approval Date: __________