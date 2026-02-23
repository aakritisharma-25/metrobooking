# 🚇 MetroBook — Metro Ticket Booking Service

A production-ready, full-stack metro ticket booking web application built as part of the MoveInSync technical assignment.

> **Live Demo:** https://metrobooking-58a4uv0zk-aakritisharma-25s-projects.vercel.app/ 

---

## ✨ Features

- User registration and login with JWT authentication
- Optimal route computation using **Dijkstra's Algorithm** with transfer penalty
- Multi-line journey support with automatic interchange detection
- Interactive metro network map using **Leaflet.js + OpenStreetMap**
- Route visualization on map after every booking
- Tamper-resistant **QR ticket generation** using SHA-256 hashing
- In-memory graph caching with Spring Cache for performance
- RESTful API with global exception handling
- PostgreSQL database with auto-created schema via Hibernate

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot, Spring Security |
| Authentication | JWT (HMAC-SHA256), BCrypt |
| Database | PostgreSQL, Hibernate JPA, HikariCP |
| Frontend | HTML5, CSS3, JavaScript, Leaflet.js |
| Build Tool | Maven |
| IDE | IntelliJ IDEA |
| Version Control | Git + GitHub (integrated in IntelliJ) |
| DB Management | pgAdmin |
| Deployment | Render (Backend), Vercel (Frontend), AWS EC2 |

---

## 🗺️ Metro Lines Supported

| Line | Route |
|---|---|
| 🟡 Yellow Line | Samaypur Badli → Huda City Centre |
| 🔵 Blue Line | Dwarka Sector 21 → Vaishali |
| 🩷 Pink Line | Janakpuri West → Lajpat Nagar |
| 🟠 Orange Line | New Delhi → IGI Airport (Express) |

---

## 🏗️ System Architecture

```
HTTP Request
   → JwtAuthFilter       (validates Bearer token, sets SecurityContext)
   → Controller          (maps endpoint, validates request body)
   → Service             (business logic, Dijkstra path finding)
   → Repository          (database read/write via JPA)
   → HTTP Response       (JSON)
```

---

## 🧠 Dijkstra's Algorithm

The core feature of this application is optimal metro path computation using Dijkstra's algorithm implemented in `PathFinderService.java`.

**Cost Function:**
```
Total Cost = Travel Time (mins) + Transfer Penalty (5 mins per interchange)
```

This ensures direct routes are always preferred over routes with unnecessary line changes. The graph is built from the database at startup and **cached in memory** using Spring Cache for fast repeated queries.

---

## 🔐 Security

- Passwords hashed with **BCrypt**
- JWT tokens signed with **HMAC-SHA256** (24hr expiry)
- All endpoints protected except `/api/auth/register` and `/api/auth/login`
- `JwtAuthFilter` intercepts every request and validates the Bearer token

---

## 🎫 QR Ticket Generation

Each booking generates a unique, tamper-resistant QR string:

```
Input  = bookingReference | sourceStopId | destinationStopId | userId | timestamp
Hash   = SHA-256(Input)
Output = bookingReference + '.' + Base64(Hash)
```

---

## 📡 API Reference

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Stops & Routes
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/stops` | Get all metro stops |
| GET | `/api/routes` | Get all metro routes |

### Bookings
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/bookings` | Create a new booking |
| GET | `/api/bookings` | Get all bookings for logged-in user |

### Sample Booking Request
```json
POST /api/bookings
{
  "sourceStopId": 4,
  "destinationStopId": 5
}
```

### Sample Response
```json
{
  "bookingReference": "MIS-20260222-A1B2C3",
  "sourceStop": "Dwarka Sector 21",
  "destinationStop": "Vaishali",
  "totalStops": 4,
  "totalInterchanges": 1,
  "estimatedTime": 14.0,
  "qrString": "MIS-20260222-A1B2C3.aGVsbG8...",
  "status": "CONFIRMED"
}
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 21+
- PostgreSQL 14+
- Maven 3.x

### Backend Setup

```bash
# 1. Clone the repository
git clone https://github.com/aakritisharma-25/metrobooking.git
cd metrobooking

# 2. Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE metrodb;"

# 3. Update application.properties with your DB password
# src/main/resources/application.properties

# 4. Run the application
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`. Tables are auto-created by Hibernate on first run.

### Frontend Setup

```bash
# Open metro-frontend/ in VS Code
# Install Live Server extension (by Ritwick Dey)
# Right click index.html → Open with Live Server
# Opens at http://127.0.0.1:5500
```

### Key Configuration (`application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/metrodb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
jwt.secret=your-secret-key
jwt.expiration=86400000
```

---

## 📁 Project Structure

```
metrobooking/
├── src/main/java/com/moveinsync/metrobooking/
│   ├── config/          # Security & cache configuration
│   ├── controller/      # REST API endpoints
│   ├── dto/             # Request/Response data transfer objects
│   ├── exception/       # Global exception handler
│   ├── graph/           # Dijkstra's algorithm & graph model
│   ├── model/           # JPA entity classes
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT filter & authentication
│   └── service/         # Business logic
├── metro-frontend/
│   ├── index.html       # Login/Register page
│   ├── dashboard.html   # Metro map dashboard
│   ├── booking.html     # Booking page
│   └── js/              # Frontend JavaScript
└── pom.xml
```

---

## 🚀 Deployment

- **Backend** deployed on **Render**
- **Frontend** deployed on **Vercel**
- Also hosted on **AWS EC2** (t3.micro, Amazon Linux 2023)
- **Live URL:** [http://13.63.108.46](http://13.63.108.46)

---

## 👩‍💻 Author

**Aakriti Sharma**
3rd Year B.Tech CSE | Lovely Professional University
MoveInSync Technical Assignment | February 2026

---

## 📄 Documentation

Full project documentation is available in [`MetroBookingService_Documentation.docx`](./MetroBookingService_Documentation.docx)
