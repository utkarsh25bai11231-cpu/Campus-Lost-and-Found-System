# Campus Lost and Found System

[**Java**]
[**MySQL**]
[**Academic Project**]
## Abstract
The **Campus Lost and Found System** is a robust, Java-based application designed to bridge the gap between students, faculty and staff who have lost personal belongings and those who have found them. In a bustling university campus, keeping track of lost items can be chaotic. This system digitizes the process, featuring automated matching, secure claim management, and role-based user management.

This project was developed applying **Object-Oriented Programming (OOP)** principles, **JDBC** for database interactions and a clean **Repository/Service architecture**.

## Features
* **Role-Based Access:** Distinct models and privileges for `Student`, `Faculty`, and `Staff`.
* **Report Management:** Users can file a `LostReport` or a `FoundReport` with detailed descriptions and categories.
* **Smart Matching Algorithm:** Background task (`MatchFinderTask`) automatically attempts to link newly found items with existing lost reports.
* **Claim Processing:** Secure flow for users to claim found items, tracked via `ClaimStatus`.
* **Notifications:** Alerts users regarding status updates on their lost items via the `NotificationCenter`.
* **Data Export:** Export reports and records using the built-in `FileExporter` utility.

## Technology Stack
* **Programming Language:** Java SE 
* **Database:** MySQL
* **Database Connectivity:** JDBC (`mysql-connector-j-8.4.0.jar`)
* **Build Tool:** Maven (`pom.xml`)
* **Design Patterns Used:** MVC Architecture, Repository Pattern, Singleton (for DB connection)

## Project Structure
Following a standard Java modular architecture:
```text
Campus-Lost-and-Found-System
 ┣ docs                  # Project UML Diagrams (Class, ER, Sequence, etc.)
 ┣ lib                   # External Dependencies (MySQL Connector)
 ┣ src/main/java/campus/lostfound
 ┃ ┣ database            # DB Connection Management
 ┃ ┣ enums               # Constants (ItemCategory, ItemStatus, etc.)
 ┃ ┣ exception           # Custom Exception Handling
 ┃ ┣ model               # POJO Classes (User, Item, Claim, etc.)
 ┃ ┣ repository          # Data Access Layer (CRUD operations)
 ┃ ┣ service             # Business Logic Layer
 ┃ ┣ util                # Helpers (DateUtil, Export, Matching Task)
 ┃ ┗ Main.java           # Application Entry Point
 ┣ schema.sql            # Database Initialization Script
 ┣ db.properties         # Database Credentials Configuration
 ┣ pom.xml               # Maven Dependencies
 ┗ compile / run scripts # Batch and PowerShell scripts for easy execution
```

## System Design & Diagrams
All UML diagrams designed for this project can be found in the `/docs` directory. They are highly recommended for project viva and documentation:
* [Architecture Diagram](docs/architecture_diagram.md)
* [Entity-Relationship (ER) Diagram](docs/er_diagram.md)
* [Class Diagram](docs/class_diagram.md)
* [Sequence Diagram](docs/sequence_diagram.md)
* [Use Case Diagram](docs/usecase_diagram.md)
* [Workflow Diagram](docs/workflow_diagram.md)

## Setup & Installation

### Prerequisites
1. **Java Development Kit (JDK 8 or higher)** installed and added to PATH.
2. **MySQL Server** installed and running.

### Step-by-Step Guide
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/YourUsername/Campus-Lost-and-Found-System.git
   cd Campus-Lost-and-Found-System
   ```

2. **Database Configuration:**
   * Open MySQL Workbench or terminal.
   * Run the `schema.sql` script to create the database and tables.
   * Open `src/main/resources/db.properties` (or the root `db.properties`) and update your MySQL username and password:
     ```properties
     db.url=jdbc:mysql://localhost:3306/campus_lost_found
     db.user=root
     db.password=your_password
     ```

3. **Compilation:**
   You can compile the project using the provided scripts (Windows):
   * Using Command Prompt: `compile.bat`
   * Using PowerShell: `./compile.ps1`
   * *Alternatively, use Maven:* `mvn clean install`

4. **Execution:**
   Run the compiled application:
   * Using Command Prompt: `run.bat`
   * Using PowerShell: `./run.ps1` 

## Future Scope
* **GUI Integration:** Migrate from a console based approach to a JavaFX or Web-based (Spring Boot) frontend.
* **AI Image Recognition:** Allow users to upload images of found items and auto-categorize them.
* **Email Integration:** Send real time email alerts instead of local console notifications.
* **Location APIs:** Integrate Google Maps API to pin exactly where an item was lost or found.
* Extend the **Rule Based** system with Machine Learning.
* Provide advanced reports and analysis.
* Add an **Administrative Dashboard**.

## Contributor
* **Utkarsh**
* B.Tech Computer Science & Engineering (Artificial Intelligence and Machine Learning) 
* **Registration Number:** 25BAI11231
* VIT Bhopal University
* https://github.com/utkarsh25bai11231-cpu/Campus-Lost-and-Found-System
   

---
