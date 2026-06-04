# Seat Management System

## Overview

Seat Management System is an enterprise workspace management platform designed to manage office seating, floor layouts, meeting room bookings, occupancy analytics, and workspace operations through interactive floor maps and real-time updates.

The platform helps organizations optimize workspace utilization, simplify seat allocation, improve visibility, and maintain complete auditability of workspace changes.

---

## Key Features

### Authentication & Authorization
- Microsoft SSO Authentication
- JWT-based Security
- Role-Based Access Control (RBAC)
- Session Management

### Workspace Management
- Seat Assignment
- Seat Reassignment
- Temporary Seat Allocation
- Team-Based Seating
- Employee Search
- Seat Search

### Floor Layout Management
- Excel-Based Layout Upload
- Layout Validation
- Migration Preview
- Layout Versioning
- Layout Activation
- Layout History
- Archived Layouts

### Interactive Floor Maps
- SVG-Based Floor Rendering
- Zoom and Pan
- Seat Highlighting
- Search Auto Focus
- Tooltip Previews
- Interactive Detail Drawers

### Meeting Room Booking
- Room Availability Tracking
- Meeting Scheduling
- Booking Conflict Prevention
- Outlook Integration
- Microsoft Teams Integration

### Analytics & Reporting
- Occupancy Analytics
- Utilization Trends
- Branch-Level Analytics
- Floor-Level Analytics
- Exportable Reports

### Audit & Compliance
- Seat Assignment History
- Layout Change History
- Meeting Booking History
- Complete Audit Logging

### Integrations
- Zoho People
- Microsoft OAuth
- Outlook Calendar
- Microsoft Teams

---

## Technology Stack

### Frontend
- React.js
- Redux Toolkit
- Tailwind CSS
- Recharts
- SVG Rendering
- WebSocket Client

### Backend
- Spring Boot
- Spring Security
- JWT Authentication
- OAuth2
- MongoDB
- Redis
- WebSocket

### Infrastructure
- Docker
- NGINX
- GitHub Actions

---

## System Architecture

Frontend (React)
↓
NGINX
↓
Spring Boot Backend
↓
MongoDB + Redis
↓
WebSocket Event Layer

External Integrations:
- Microsoft SSO
- Zoho People
- Outlook Calendar
- Microsoft Teams

---

## User Roles

### SUPER_ADMIN
- Full system administration
- User management
- Configuration management
- Access control

### HR_ADMIN
- Seat operations
- Layout management
- Analytics access
- Employee workspace management

### MANAGER
- Team seat management
- Team visibility
- Team workspace allocation

### EMPLOYEE
- View floor maps
- Search employees and seats
- Book meeting rooms
- View assigned workspace

---

## Core Modules

- Authentication Module
- Employee Module
- Role Management Module
- Branch Management Module
- Floor Management Module
- Layout Management Module
- Seat Management Module
- Meeting Room Module
- Analytics Module
- Audit Module
- Search Module
- Notification Module

---

## Project Structure

seat-management-system/

├── frontend/
│
├── backend/
│
├── docs/
│   ├── PRD.md
│   ├── Business-Rules.md
│   ├── HLD.md
│   ├── LLD.md
│   ├── API-Specification.md
│   ├── Security-Architecture.md
│   ├── Deployment-Architecture.md
│   ├── UI-UX-Specification.md
│   ├── Test-Strategy.md
│   ├── Release-Strategy.md
│   └── ADR.md
│
├── database/
│
├── architecture/
│
├── docker/
│
├── scripts/
│
└── README.md

---

## Non-Functional Requirements

- Enterprise Grade Security
- High Availability
- Real-Time Synchronization
- Full Auditability
- Scalable Architecture
- Responsive User Interface
- Performance Optimized Rendering
- Support for 1000+ Employees

---

## Future Enhancements

- Workspace Heatmaps
- AI-Based Seat Recommendations
- Hybrid Work Scheduling
- Visitor Management
- Mobile Application
- Advanced Reporting Dashboard

---

## Version

Current Version: v1.0.0

Status: Active Development

---

## License

MIT License

---

## Author

Bhagyashree

Enterprise Workspace Management Platform

Built using React, Spring Boot, MongoDB, Redis, WebSockets, RBAC, and Microsoft SSO.
